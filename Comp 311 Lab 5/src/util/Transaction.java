package util;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a transaction
 * @author tim
 * @version 1.0
 *
 */
public class Transaction {
    private String id;
    private List<TxInput> inputs = new ArrayList<>();
    private List<TxOutput> outputs = new ArrayList<>();

    /**
     * Creates a transaction
     * @param id the id
     */
    public Transaction(String id) {
        super();
        this.id = id;
    }

    /**
     * Adds an input
     * @param input the input
     */
    public void addInput(TxInput input) {
        inputs.add(input);
    }

    /**
     * Adds an output
     * @param output the output
     */
    public void addOutput(TxOutput output) {
        outputs.add(output);
    }

    /**
     * Returns the id
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the inputs
     * @return the inputs
     */
    public List<TxInput> getInputs() {
        return inputs;
    }

    /**
     * Returns the outputs
     * @return the outputs
     */
    public List<TxOutput> getOutputs() {
        return outputs;
    }

    @Override
    public boolean equals(Object obj) {
        Transaction that = (Transaction)obj;
        return this.id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
