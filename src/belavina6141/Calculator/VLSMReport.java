package belavina6141.Calculator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Olga Belavina on 2016-04-01.
 */

/*

 Network address (CIDR format)
 belavina6141.Calculator.Subnet mask (dotted decimal notation)
 Broadcast address
 Host pool size and range

 */


public class VLSMReport implements Iterable<VLSMReportSubnet>{

    private  long   hostsAvailable;
    private  long   hostUsed;
    private  long   hostsNeeded;

    private List<VLSMReportSubnet> reportSubnets = new ArrayList<>();

    public long getHostsAvailable() {
        return hostsAvailable;
    }

    public void setHostsAvailable(long hostsAvailable) {
        this.hostsAvailable = hostsAvailable;
    }

    public long getHostUsed() {
        return hostUsed;
    }

    public void setHostUsed(long hostUsed) {
        this.hostUsed = hostUsed;
    }

    @Override
    public Iterator<VLSMReportSubnet> iterator() {
        return reportSubnets.iterator();
    }

    public List<VLSMReportSubnet> getReportSubnets() {
        return reportSubnets;
    }

    public void setReportSubnets(List<VLSMReportSubnet> reportSubnets) {
        this.reportSubnets = reportSubnets;
    }

    public long getHostsNeeded() {
        return hostsNeeded;
    }

    public void setHostsNeeded(long hostsNeeded) {
        this.hostsNeeded = hostsNeeded;
    }
}

