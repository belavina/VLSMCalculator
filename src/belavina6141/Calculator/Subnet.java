package belavina6141.Calculator;

import java.util.Comparator;

/**
 * Created by Olga Belavina on 2016-04-01.
 */


public class Subnet {

    public static final Comparator<Subnet> BY_HOSTS = Comparator.comparing(Subnet::getNumOfHosts);
    public static final Comparator<Subnet> BY_MODE = Comparator.comparing(Subnet::getMode);

    private  long numOfHosts;
    private  String name = new String();
    private  int    mode;



    public Subnet(String name, long numOfHosts, int mode){
        this.numOfHosts = numOfHosts;
        this.name = name;
        this.mode = mode;
    }

    public Subnet(int numOfHosts, int mode) {
        this.numOfHosts = numOfHosts;
        this.mode = mode;
    }


    public long getNumOfHosts() {
        return numOfHosts;
    }

    public void setNumOfHosts(long numOfHosts) {
        this.numOfHosts = numOfHosts;
    }

    @Override
    public String toString() {
        return "belavina6141.Calculator.Subnet{" +
                "numOfHosts=" + numOfHosts +
                ", name='" + name + '\'' +
                ", mode=" + mode +
                '}';
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
