package util;


/**
 * Represents an input or output in a transaction
 * @author tim
 * @version 1.0
 *
 */
public abstract class TxIO {

    private String address;

    /**
     * Creates a TxIO
     * @param address the address
     */
    public TxIO(String address) {
        super();
        this.address = address;
    }

    /**
     * Returns the address
     * @return the address
     */
    public String getAddress() {
        return address;
    }
}
