import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import util.Transaction;
import util.TxInput;
import util.TxOutput;

/**
 * Tests for transaction processor
 * @author jacob
 * @version 1.0
 *
 */
public class TransactionProcessorTest {
    private List<Transaction> txs;
    private Transaction tx1;
    private Transaction tx2;
    private Transaction tx3;
    private Transaction tx4;
    private Transaction tx5;

    /**
     * Sets up tests
     */
    @Before
    public void setUp() {
        txs = new ArrayList<Transaction>();
        tx1 = new Transaction("tx1");
        tx1.addOutput(new TxOutput("tx1Out"));
        tx2 = new Transaction("tx2");
        tx2.addInput(new TxInput("tx1Out"));
        tx2.addOutput(new TxOutput("tx2Out1"));
        tx2.addOutput(new TxOutput("tx2Out2"));
        tx3 = new Transaction("tx3");
        tx3.addOutput(new TxOutput("tx3Out"));
        tx4 = new Transaction("tx4");
        tx4.addInput(new TxInput("tx3Out"));
        tx4.addOutput(new TxOutput("tx4Out1"));
        tx4.addOutput(new TxOutput("tx4Out2"));
        tx5 = new Transaction("tx5");
        tx5.addInput(new TxInput("tx2Out1"));
        tx5.addInput(new TxInput("tx4Out1"));
        tx5.addOutput(new TxOutput("tx5Out"));

        txs.add(tx1);
        txs.add(tx2);
        txs.add(tx3);
        txs.add(tx4);
        txs.add(tx5);
    }

    /**
     * Makes sure the transaction processor
     * throws exception if there's a duplicate.
     */
    @Test
    public void testConstructorDuplicates() {
        // List without duplicates
        boolean caught = false;
        try {
            TransactionProcessor tp = new TransactionProcessor(txs);
            tp.getDependencies(0);
        }
        catch (Exception e) {
            caught = true;
        }
        assertFalse(caught);

        // List with duplicate
        tx2.addOutput(new TxOutput("tx2Out2"));

        caught = false;
        try {
            TransactionProcessor tp = new TransactionProcessor(txs);
            tp.getDependencies(0);
        }
        catch (Exception e) {
            caught = true;
        }
        assertTrue(caught);
    }

    /**
     * Makes sure the transaction processor
     * throws exception if there's an output reused.
     */
    @Test
    public void testConstructorReused() {
        // List with output reused
        tx5.addInput(new TxInput("tx3Out"));
        boolean caught = false;
        try {
            TransactionProcessor tp = new TransactionProcessor(txs);
            tp.getDependencies(0);
        }
        catch (Exception e) {
            caught = true;
        }
        assertTrue(caught);
    }

    /**
     * Tests vertex adds correct other vertices it
     * is dependant on.
     */
    @Test
    public void testGraph() {
        try {
            TransactionProcessor tp = new TransactionProcessor(txs);

            assertTrue(tp.getDependencies(0).isEmpty());

            List<Transaction> list1 = new ArrayList<Transaction>();
            list1.add(tx1);
            assertEquals(list1, tp.getDependencies(1));

            assertTrue(tp.getDependencies(2).isEmpty());

            List<Transaction> list2 = new ArrayList<Transaction>();
            list2.add(tx3);
            assertEquals(list2, tp.getDependencies(3));

            List<Transaction> list3 = new ArrayList<Transaction>();
            list3.add(tx2);
            list3.add(tx4);
            assertEquals(list3, tp.getDependencies(4));
        }
        catch (DuplicateOutputException e) {
            e.printStackTrace();
        }
        catch (OutputReusedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test if upstream returned correct order
     */
    @Test
    public void testUpstream() {
        try {
            TransactionProcessor tp = new TransactionProcessor(txs);

            List<Transaction> list = new ArrayList<Transaction>();
            list.add(tx1);

            assertEquals(list, tp.getUpstreamTransactions(tx1));

            list.add(tx2);
            assertEquals(list, tp.getUpstreamTransactions(tx2));

            List<Transaction> list2 = new ArrayList<Transaction>();
            list2.add(tx3);
            assertEquals(list2, tp.getUpstreamTransactions(tx3));

            list2.add(tx4);
            assertEquals(list2, tp.getUpstreamTransactions(tx4));

            List<Transaction> list3 = new ArrayList<Transaction>();
            list3.add(tx3);
            list3.add(tx4);
            list3.add(tx1);
            list3.add(tx2);
            list3.add(tx5);
            assertEquals(list3, tp.getUpstreamTransactions(tx5));
        }
        catch (DuplicateOutputException e) {
            e.printStackTrace();
        }
        catch (OutputReusedException e) {
            e.printStackTrace();
        }
        catch (MissingTxException e) {
            e.printStackTrace();
        }
        catch (CycleDetectedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests cycle is detected
     */
    @Test
    public void testCycle() {
        try {
            tx1.addInput(new TxInput("tx2Out2"));
            TransactionProcessor tp = new TransactionProcessor(txs);

            boolean caught = false;
            try {
                tp.getUpstreamTransactions(tx5);
            }
            catch (Exception e) {
                caught = true;
            }
            assertTrue(caught);

        }
        catch (DuplicateOutputException e) {
            e.printStackTrace();
        }
        catch (OutputReusedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test if transaction is missing
     */
    @Test
    public void testMissing() {
        try {
            tx2.addInput(new TxInput("tx6Out"));
            TransactionProcessor tp = new TransactionProcessor(txs);

            boolean caught = false;
            try {
                tp.getUpstreamTransactions(tx5);
            }
            catch (Exception e) {
                caught = true;
            }
            assertTrue(caught);

        }
        catch (DuplicateOutputException e) {
            e.printStackTrace();
        }
        catch (OutputReusedException e) {
            e.printStackTrace();
        }
    }
}
