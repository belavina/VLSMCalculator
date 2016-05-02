package belavina6141.Calculator;

import belavina6141.Controller.VLSMController;

import java.util.*;

/**
 * Created by Olga Belavina on 2016-03-30.
 */


public class VLSMCalculator {

    public static Integer MODE_MINIMUM  = 0;
    public static Integer MODE_MAXIMUM  = 1;
    public static Integer MODE_BALANCED = 2;

    private int[] tokens;

    /* mask in cidr notation*/
    private int    mask;
    /* Information about subnets to be created  */
    private List<Subnet> subnets = new ArrayList<>();

    /* {255,255,255,255}  becomes "255.255.255.255" */

    /**
     * Returns String in the format 255.255.255.255 from array {255,255,255,255}
     *
     * @param arr array of integers,
     *
     * @return new String
     */
    public static String parseIntoIPv4Format(int[] arr) {
        String address = new String();
        if (arr.length == 4) {
            for (int i = 0; i < (arr.length-1); i++) {
                address += String.valueOf(arr[i]) + ".";
            }
            address += arr[3];

            return address;
        } else {
            return null;
        }
    }


    /**
     * Total umber of host that can be used.
     *
     * @param cidr Mask in cidr notation
     * @return number of addresses available on the network
     */
    public static long numberOfHostsAvailable(int cidr){
        return (long)Math.pow(2, (32-cidr));
    }


    /**
     * Finds the next address : 192.1.2.5 -> 192.1.2.6
     *
     * @param add IPv4 address
     * @return next address
     */
    public static int[] getNextAddress(int add[]){

        int[] nextAddress = new int[3];
        nextAddress =  Arrays.copyOfRange(add, 0, add.length);

        if (add[3] < 255) {
            nextAddress[3]++;
        } else if (add[2] < 255){
            nextAddress[2]++;
            nextAddress[3] = 0;
        } else if (add[1] < 255){
            nextAddress[1]++;
            nextAddress[2] = 0;
            nextAddress[3] = 0;
        } else if (add[0] < 255) {
            nextAddress[0]++;
            nextAddress[1] = 0;
            nextAddress[2] = 0;
            nextAddress[3] = 0;
        }

        return nextAddress;
    }

    /**
     * Adds hosts to IP address
     *
     * @param ip IPv4 address
     * @param hosts number of hosts to be added to the address
     *
     * @return new IPv4 address
     */
    public static int[] addHostsToIp(int[] ip, long hosts){
        int[] nextAddress = new int[3];
        nextAddress =  Arrays.copyOfRange(ip, 0, ip.length);

        int binary;
        binary = ip[0];
        binary = (binary << 8) + ip[1];
        binary = (binary << 8) + ip[2];
        binary = (binary << 8) + ip[3];

        long sum = binary+hosts;

        nextAddress[0] = (int)(sum >> 24) & 255;
        nextAddress[1] = (int)(sum >> 16) & 255;
        nextAddress[2] = (int)(sum >> 8) & 255;
        nextAddress[3] = (int)sum & 255;

        return nextAddress;
    }


    /**
     * Finds out minimum space required
     *
     * @param minSubnets list of subnets
     * @return total minimum number of hosts
     */
    private long findOutMinimum(List<Subnet> minSubnets){

        long approxNumOfHosts = 0;
        for (int j = 0; j < minSubnets.size(); j++) {
            approxNumOfHosts += minimumForOneSubnet(minSubnets.get(j));
        }
        return approxNumOfHosts;
    }

    /**
     * Finds out minimum number of hosts for a single subnet
     *
     * @param subnet subnet
     * @return minimum number of hosts
     */
    private long minimumForOneSubnet(Subnet subnet){

        long approxNumOfHosts = 0;
        long hosts  = subnet.getNumOfHosts();

        for (int i = 0; i < 32 && ((32 - i) >= mask); i++) {  // 8 + 8 + 8 + 8
            long value = (long)Math.pow(2, i);
            if (hosts < value && (value - hosts) >= 2) {
                approxNumOfHosts += (long) value;
                break;
            }
        }

        return approxNumOfHosts;
    }

    /**
     * Resets hosts for subnets with balanced mode based on number of hosts available [stored in subnet member variable]
     *
     * @param balSubnets list of subnets with balanced mode
     * @param leftOvers  hosts available
     *
     */
    private void findHostsForBalanced(List<Subnet> balSubnets, long leftOvers){


        while ((balSubnets.get(balSubnets.size() - 1).getNumOfHosts()) < (leftOvers)) {

            for (int i = 0; i < balSubnets.size(); i++ ) {
                if((balSubnets.get(i).getNumOfHosts()) <= leftOvers ){
                    leftOvers = leftOvers - (balSubnets.get(i).getNumOfHosts() + 2);
                    balSubnets.get(i).setNumOfHosts(((balSubnets.get(i).getNumOfHosts()+2) * 2) - 2 );
                }
            }

        }
    }




    /**
     * Performs subnetting using IP Address and information for each subnet
     *
     * @return VLSMreport, which includes subnet name, network addresse, hosts available, range
     *          & broadcast for each subnet
     */
    public VLSMReport calculateVLSM() {

        VLSMReport vlsmReport    = new VLSMReport();
        List<VLSMReportSubnet> vlsmReportSubnets = new ArrayList<>();

        /* The only max subnet */
        Subnet maxSubnet = null;

        /* First balanced Subnet */
        Subnet balSubnet = null;

        long hostsAvailable = numberOfHostsAvailable(mask);

        // Sort by the mode :
        Collections.sort(subnets, Subnet.BY_MODE);  // Order 0(MIN) , 1(MAX) , 2(BAL)
        Iterator<Subnet> itr = subnets.iterator();

        /* Find if any subnet has MAXIMUM_MODE -> if not, subnets with balanced mode will have more space*/
        /* Find total number of hosts (required) */
        /* Initial set up  */
        long sumNeeded    = 0;

        while (itr.hasNext()) {
            Subnet s = itr.next();
            sumNeeded  += s.getNumOfHosts();

            if(s.getMode() == MODE_MAXIMUM) {
                maxSubnet = s;
            }

            if(balSubnet == null && s.getMode() == MODE_BALANCED ){
                balSubnet = s;
            }
        }

        vlsmReport.setHostsNeeded(sumNeeded);

        // If last one on the list.mode == MODE_MINIMUM
        // sort BY_HOSTS.reversed();
        // minimum for everybody
        if (!subnets.isEmpty() && subnets.get(subnets.size()-1).getMode() != MODE_MINIMUM) {

             /* Approx number of hosts taken by other subnets*/
            long approxNumOfHosts = 0;

            // if any subnet has max value
            // Find out how much minimum will take
            // Find out how much balanced will take -> balanced 2^7 -> 2^8 (power+1)
            // Use the remainder to calculate the max
            if (maxSubnet != null) {

                subnets.indexOf(maxSubnet);

                /* Get SubList of all the minimum subnets */
                List<Subnet> minSubnets = subnets.subList(0,subnets.indexOf(maxSubnet));
                minSubnets.sort(Subnet.BY_HOSTS.reversed());

                /* Get SubList of all the balanced subnets */
                List<Subnet> balSubnets = subnets.subList(subnets.indexOf(maxSubnet)+1, subnets.size());
                balSubnets.sort(Subnet.BY_HOSTS.reversed());

                long approxNumOfHostsMin = 0;
                long approxNumOfHostsBal = 0;
                // == Calculating hosts for subnets with MIN value : ==

                approxNumOfHostsMin = findOutMinimum(minSubnets);
                approxNumOfHosts += approxNumOfHostsMin;

                // == Calculating hosts for subnets with BAL value  -> : ==
                if(!balSubnets.isEmpty()) {

                    long maxHosts = maxSubnet.getNumOfHosts();

                    // Find out minimum number of hosts for subnet with MAX Value :
                    long leftOvers = 0 ;
                    leftOvers = hostsAvailable - ( approxNumOfHostsMin + minimumForOneSubnet(maxSubnet));

                    /* Minimum for subnets with balanced mode */
                    for (int j = 0; j < balSubnets.size(); j++) {
                        long hosts = balSubnets.get(j).getNumOfHosts();

                        for (int i = 0; i < 32 && ((32 - i) >= mask); i++) {  // 8 + 8 + 8 + 8
                            long value = (long)Math.pow(2, i);
                            if (hosts < value && (value - hosts) >= 2) {

                                balSubnets.get(j).setNumOfHosts( value-2 );
                                approxNumOfHostsBal += value;

                                break;
                            }
                        }
                    }

                     leftOvers -= approxNumOfHostsBal;

                    // increase host size of subnets with balanced mode if possible
                   /* Balanced mode -> 2^6 becomes  2^7 if possible*/
                    for (int i = 0; i < balSubnets.size(); i++) {
                        if ((balSubnets.get(i).getNumOfHosts() * 2) <= leftOvers) {
                            /* initial number of hosts for each subnet is multiplied by 2*/
                            leftOvers = leftOvers - (balSubnets.get(i).getNumOfHosts());
                            approxNumOfHostsBal += balSubnets.get(i).getNumOfHosts();
                            long value = (balSubnets.get(i).getNumOfHosts() + 2) * 2 - 2;
                            balSubnets.get(i).setNumOfHosts(value);
                        }
                    }

                    approxNumOfHosts = approxNumOfHostsBal + approxNumOfHostsMin;
                }

                // == Set Max hosts to be equal to  ==

                long hosts = maxSubnet.getNumOfHosts();
                for (int i = 31; i >= 0; i--) {
                    long value = (long)Math.pow(2, i);

                    if ( (value - hosts) >= 2
                                && value <= (hostsAvailable - approxNumOfHosts )) {
                        approxNumOfHosts += value;
                        maxSubnet.setNumOfHosts(value-2);
                        break;
                    }
                }

                // Recalculate balanced
                if(!balSubnets.isEmpty()) {
                    long leftOvers = hostsAvailable - approxNumOfHosts;

                    /* If host space is left - > further increase sizes of subnets with balanced mode*/
                    findHostsForBalanced(balSubnets, leftOvers);
                }

                // if it has only balanced mode :
                // Find out how much minimum takes
                // Use the remainder to calculate all the subnets with balanced mode
            } else if (!subnets.isEmpty() && subnets.get(subnets.size()-1).getMode() == MODE_BALANCED) {

                // Find out how much minimum takes
                /* Get SubList of all the minimum subnets */
                List<Subnet> minSubnets = subnets.subList(0,subnets.indexOf(balSubnet));
                minSubnets.sort(Subnet.BY_HOSTS.reversed());

                approxNumOfHosts = findOutMinimum(minSubnets);

                /* Get SubList of all the balanced subnets */
                List<Subnet> balSubnets = subnets.subList(subnets.indexOf(balSubnet), subnets.size());
                balSubnets.sort(Subnet.BY_HOSTS.reversed());

                /* Find out minimum required for Subnets with Balanced mode */
                for (int j = 0; j < balSubnets.size(); j++) {
                    long hosts = balSubnets.get(j).getNumOfHosts();

                    for (int i = 0; i < 32 && ((32 - i) >= mask); i++) {  // 8 + 8 + 8 + 8
                        long value = (long)Math.pow(2, i);
                        if (hosts < value && (value - hosts) >= 2) {

                            balSubnets.get(j).setNumOfHosts( value-2 );
                            approxNumOfHosts += value;

                            break;
                        }
                    }
                }


                long leftOvers = hostsAvailable - approxNumOfHosts;
                /* If host space is left - >  increase sizes of subnets with balanced mode*/
                findHostsForBalanced(balSubnets, leftOvers);
            }
        }   // else : standard VLSM calculation -> minimum for everybody


        /* Sorting */
        Collections.sort(subnets, Subnet.BY_HOSTS.reversed());

        /* Total number of hosts */
        long sumAllocated = 0;

        int[] networkAddress = findNetworkID(tokens, findMaskFromCIDR(mask));
        int[] lastAddress    = findNetworkID(tokens, findMaskFromCIDR(mask)) ;


            for (int j = 0; j < subnets.size(); j++) {

                long hosts = subnets.get(j).getNumOfHosts();

                VLSMReportSubnet report = new VLSMReportSubnet();

                long hostAllocated = 0;
                int maskCIDR = 0;
                int[] subnetMask;
                int[] broadcast = new int[3];


                for (int i = 0; i < 32 && ((32 - i) >= mask); i++) {  // 8 + 8 + 8 + 8

                    long value =  (long)Math.pow(2, i);
                    if (hosts < value && (value - hosts) >= 2) {

                        hostAllocated = (long)value;
                        maskCIDR = 32 - (i);
                        subnetMask = findMaskFromCIDR(maskCIDR);



                        /* hostAllocated - (BroadcastAddress + netAddress) */
                        sumAllocated += hostAllocated;

                        report.setHosts(hostAllocated - 2);
                        report.setSubnetName(subnets.get(j).getName());
                        report.setNetAddress(VLSMCalculator.parseIntoIPv4Format(networkAddress) + "/" + maskCIDR);
                        report.setSubnetMask(VLSMCalculator.parseIntoIPv4Format(subnetMask));


                        lastAddress = addHostsToIp(lastAddress, hostAllocated-2);

                        /* Broadcast comes after lastAddress */
                        broadcast = getNextAddress(lastAddress);
                        report.setBroadcastAddress(VLSMCalculator.parseIntoIPv4Format(broadcast));


                        report.setRange(VLSMCalculator.parseIntoIPv4Format(getNextAddress(networkAddress)) + " - " +
                                VLSMCalculator.parseIntoIPv4Format(lastAddress));


                        lastAddress = getNextAddress(broadcast);
                       /* New network address */
                        networkAddress = getNextAddress(broadcast);

                        vlsmReportSubnets.add(report);
                        break;
                    }
                }
            }


        vlsmReport.setHostUsed(sumAllocated);
        vlsmReport.setHostsAvailable(hostsAvailable);

        vlsmReport.setReportSubnets(vlsmReportSubnets);

        return vlsmReport;
    }


    /**
     * Checks if tokens provided are valid
     *
     * @return true if valid
     */
    private boolean tokensAreValid() {
        for (int t : tokens) {
            if (t < 0 || t > 255) {
                return false;
            }
        }
        return true;
    }


    /**
     * Finds network Id using IP address & mask
     *
     * @return Network id
     */
    static public int[] findNetworkID(int[] ip, int[] msk) {

        int[] networkId = new int[4];

        for ( int i = 0; i < 4; i++) {
            networkId[i] = ip[i] & msk[i];
        }

        return networkId;
    }


    /**
     *  Converts CIDR to dotted decimal notation e.g  /27 becomes 255.255.255.224
     *
     * @param cidr mask in CIDR format (e.g /27)
     *
     * @return mask in dotted decimal notation
     */
    static public int[] findMaskFromCIDR( int cidr ){

        int m[] = new int[4];

        if (cidr >= 24) {
            m[0] = 255;
            m[1] = 255;
            m[2] = 255;

            m[3] = 256 - (1 << (32 - cidr));
        } else if (cidr >= 16) {
            m[0] = 255;
            m[1] = 255;

            m[2] = 256 - (1 << (24 - cidr));
        } else if (cidr >= 8) {
            m[0] = 255;
            m[1] = 256 - (1 << (16 - cidr));
        } else  {
            m[0] = 256 - (1 << (8 - cidr));
        }


        return m;
    }



    public void setSubnets(List<Subnet> s){
        subnets = s;
    }

    public void addSubnet(List<Subnet> s){
        subnets.addAll(s);
    }

    public void addSubnet(Subnet subnet){
        subnets.add(subnet);
    }

    public  VLSMCalculator(){}


    /**
     * Sets new network id, validates the format, retrieves CIDR (mask) and tokens
     *
     *  @param ipV4 IP Address e.g "192.168.1.0/22"
     *
     */
    public void addNetwork(String ipV4){
        String[] strTokens = ipV4.split("\\.");

        try {

            this.tokens = new int[strTokens.length];
            try {

                mask = Integer.parseInt(strTokens[strTokens.length - 1].split("\\/")[1]);
                strTokens[strTokens.length - 1] = strTokens[strTokens.length - 1].split("\\/")[0];

                for (int i = 0; i < strTokens.length; i++) {
                    tokens[i] = Integer.parseInt(strTokens[i]);
                }

                /* === Validation === */
                if(!tokensAreValid()) {
                    throw new InvalidIPv4Exception();
                }

                if (mask < 0 || mask > 32) {
                    throw new InvalidIPv4Exception();
                }

            } catch (NumberFormatException e) {
                throw new InvalidIPv4Exception();
            }

        } catch (IndexOutOfBoundsException e) {
            throw new InvalidIPv4Exception();
        }
    }


    /**
     * Sets new network id, validates the format, retrieves CIDR (mask) and tokens
     *
     *  @param ipV4 IP Address e.g "192.168.1.0/22"
     *
     */
    public  VLSMCalculator(String ipV4) {
        addNetwork(ipV4);
    }

    public int[] getTokens() {
        return tokens;
    }

    public int getMask() {
        return mask;
    }


    /**
     * Displays subnets to the console
     *
     *  @param subnets Subnets provided
     *
     */
    static public void PrintHosts(List<Subnet> subnets){
        for(Subnet s : subnets) {
            System.out.println(s.getName() + " : hosts : " + s.getNumOfHosts());
        }
    }


    static public void main(String[] args) {

        VLSMCalculator vlsmCalculator = new VLSMCalculator("10.1.2.0/2");
        PrintHosts(vlsmCalculator.subnets);
    }



}
