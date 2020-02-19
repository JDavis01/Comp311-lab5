import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import util.Transaction;
import util.TxInput;
import util.TxOutput;

/**
 * Tracks down the origin of the
 * bitcoins in a particular transaction.
 * @author jacob
 * @version 1.0
 *
 */
public class TransactionProcessor {
    private List<Vertex> vertices;
    private Stack<Transaction> stack;

    /**
     * Constructs a transaction processor
     * @param transactions list of transactions
     * @throws DuplicateOutputException thrown if duplicate
     * @throws OutputReusedException thrown if output reused
     */
    public TransactionProcessor(List<Transaction> transactions)
        throws DuplicateOutputException, OutputReusedException {
        Set<String> outAddrSet = new HashSet<String>();
        Set<String> inAddrSet = new HashSet<String>();
        List<String> outAddrList = new ArrayList<String>();
        List<String> inAddrList = new ArrayList<String>();
        this.vertices = new ArrayList<Vertex>();
        this.stack = new Stack<Transaction>();

        for (Transaction tx : transactions) {
            Vertex v = new Vertex(tx);
            this.vertices.add(v);
            for (TxOutput out : tx.getOutputs()) {
                outAddrSet.add(out.getAddress());
                outAddrList.add(out.getAddress());
            }
            for (TxInput in : tx.getInputs()) {
                inAddrSet.add(in.getAddress());
                inAddrList.add(in.getAddress());
            }
        }

        // Check for duplicates
        if (outAddrSet.size() != outAddrList.size()) {
            throw new DuplicateOutputException();
        }
        // Check if outputs were reused
        if (inAddrSet.size() != inAddrList.size()) {
            throw new OutputReusedException();
        }

        // Look for dependants and add them to vertices
        for (Vertex vi : vertices) {
            for (String addr : vi.getIns()) {
                for (Vertex vj : vertices) {
                    if (vj.getOuts().contains(addr)) {
                        vi.addSupporter(vj);
                    }
                }
            }
        }
    }

    /**
     * Used for testing to see if txs have correct dependencies
     * @param i vertex to test
     * @return List<Transaction> list of dep
     */
    public List<Transaction> getDependencies(int i) {
        Vertex v = this.vertices.get(i);
        List<Transaction> supporters = new ArrayList<Transaction>();
        for (Vertex dep : v.getDependencies()) {
            supporters.add(dep.getData());
        }
        return supporters;
    }

    /**
     * Sorts transactions for get upstream
     * @param tx to start from
     * @return LinkedList<Transaction> list of txs
     * @throws CycleDetectedException
     */
    private Stack<Transaction> sort(Transaction tx)
        throws CycleDetectedException {

        Vertex v = null;
        for (Vertex vi : vertices) {
            if (vi.getData().equals(tx)) {
                v = vi;
                if (!v.isVisited()) {
                    v.setCount(v.getIns().size());
                }
                break;
            }
        }
        if (!stack.contains(v.getData())) {
            stack.push(tx);
            v.setVisitationTrue();
        }
        List<Vertex> supporters = v.getDependencies();
        for (int i = 0; i < supporters.size(); i++) {
            Vertex vs = supporters.get(i);
            v.decrementCount();
            if (vs.isVisited() && stack.contains(vs.getData())
                && v.getCounter() == 0) {
                throw new CycleDetectedException();
            }
            if (!vs.isVisited()) {
                sort(vs.getData());
            }

        }

        return stack;
    }

    /**
     * Traces back to the origin from this transaction
     * @param tx transaction to trace
     * @return List<Transaction> return a list txs
     * @throws MissingTxException thrown if tx missing
     * @throws CycleDetectedException thrown if cycle exists
     */
    public List<Transaction> getUpstreamTransactions(Transaction tx)
        throws MissingTxException, CycleDetectedException {
        Stack<Transaction> s = sort(tx);
        LinkedList<Transaction> sorted = new LinkedList<Transaction>();
        while (!s.isEmpty()) {
            sorted.addLast(s.pop());
        }
        for (Vertex v : vertices) {
            v.setVisitationFalse();
            if (v.getCounter() > 0) {
                throw new MissingTxException();
            }
        }

        return sorted;
    }

    /**
     * Represents a vertex in a graph
     * @author jacob
     * @version 1.0
     *
     */
    private static class Vertex {
        private Transaction v;
        private List<String> ins;
        private String outs;
        private List<Vertex> dependencies;
        private boolean visited;
        private int counter;

        /**
         * Constructs a vertex
         * @param tx a transaction
         */
        public Vertex(Transaction tx) {
            this.v = tx;
            this.dependencies = new ArrayList<Vertex>();
            this.ins = new ArrayList<String>();
            this.outs = "";
            this.visited = false;
            this.counter = 0;

            for (TxInput in : tx.getInputs()) {
                ins.add(in.getAddress());
            }

            for (TxOutput out : tx.getOutputs()) {
                outs += out.getAddress();
            }
        }

        /**
         * Gets count of inputs to process
         * @return int amount to process
         */
        public int getCounter() {
            return this.counter;
        }

        /**
         * Sets the amount to process
         * @param c the count
         */
        public void setCount(int c) {
            this.counter = c;
        }

        /**
         * Decrements the counter
         */
        public void decrementCount() {
            this.counter--;
        }

        /**
         * Returns true if visited
         * @return boolean a flag
         */
        public boolean isVisited() {
            return this.visited;
        }

        /**
         * Sets vertex visit status to true
         */
        public void setVisitationTrue() {
            this.visited = true;
        }

        /**
         * Sets vertex visit status to false
         */
        public void setVisitationFalse() {
            this.visited = false;
        }

        /**
         * Returns the data of the vertex
         * @return Transaction the data
         */
        public Transaction getData( ) {
            return this.v;
        }

        /**
         * Returns the dependencies of this vertex
         * @return List<Vertex> the list
         */
        public List<Vertex> getDependencies() {
            return this.dependencies;
        }

        /**
         * Returns the list of incoming edges
         * @return List<String> the list
         */
        public List<String> getIns() {
            return this.ins;
        }

        /**
         * Returns the the outcoming edges as a string
         * @return String the edges combined
         */
        public String getOuts() {
            return this.outs;
        }

        /**
         * Adds supporters to vertex
         * @param support the supporter
         */
        public void addSupporter(Vertex support) {
            this.dependencies.add(support);
        }
    }
}
