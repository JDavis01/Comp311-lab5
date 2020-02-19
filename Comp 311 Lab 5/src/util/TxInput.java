package util;


/**
 * A transaction input
 * @author tim
 * @version 1.0
 *
 */
public class TxInput extends TxIO {
    private TxOutput spentOutput;

    /**
     * Creates the input
     * @param address the address
     */
    public TxInput(String address) {
        super(address);
    }

    /**
     * Returns the TxOutput that was spent by this input
     * @return the TxOutput that was spent by this input
     */
    public TxOutput getSpentOutput() {
        return spentOutput;
    }

    /**
     * Sets the spent output
     * @param source the spent output
     */
    public void setSpentOutput(TxOutput source) {
        this.spentOutput = source;
    }
}
