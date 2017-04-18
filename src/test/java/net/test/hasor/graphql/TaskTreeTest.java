package net.test.hasor.graphql;
import net.hasor.graphql.dsl.QueryModel;
import net.hasor.graphql.runtime.QueryTask;
import net.hasor.graphql.runtime.TaskParser;
import org.junit.Test;
/**
 * Created by yongchun.zyc on 2017/3/21.
 */
public class TaskTreeTest {
    @Test
    public void main1() {
        QueryModel queryModel = new GraphQLTest().main1();
        QueryTask queryTask = new TaskParser().doParser(queryModel.getDomain());
        this.printTaskTree(queryTask);
    }
    @Test
    public void main2() {
        QueryModel queryModel = new GraphQLTest().main2();
        QueryTask queryTask = new TaskParser().doParser(queryModel.getDomain());
        this.printTaskTree(queryTask);
    }
    @Test
    public void main3() {
        QueryModel queryModel = new GraphQLTest().main3();
        QueryTask queryTask = new TaskParser().doParser(queryModel.getDomain());
        this.printTaskTree(queryTask);
    }
    @Test
    public void main4() {
        QueryModel queryModel = new GraphQLTest().main4();
        QueryTask queryTask = new TaskParser().doParser(queryModel.getDomain());
        this.printTaskTree(queryTask);
    }
    @Test
    public void main5() {
        QueryModel queryModel = new GraphQLTest().main5();
        QueryTask queryTask = new TaskParser().doParser(queryModel.getDomain());
        this.printTaskTree(queryTask);
    }
    @Test
    public void main6() {
        QueryModel queryModel = new GraphQLTest().main6();
        QueryTask queryTask = new TaskParser().doParser(queryModel.getDomain());
        this.printTaskTree(queryTask);
    }
    @Test
    public void main7() {
        QueryModel queryModel = new GraphQLTest().main7();
        QueryTask queryTask = new TaskParser().doParser(queryModel.getDomain());
        this.printTaskTree(queryTask);
    }
    @Test
    public void main8() {
        QueryModel queryModel = new GraphQLTest().main8();
        QueryTask queryTask = new TaskParser().doParser(queryModel.getDomain());
        this.printTaskTree(queryTask);
    }
    @Test
    public void main9() {
        QueryModel queryModel = new GraphQLTest().main9();
        QueryTask queryTask = new TaskParser().doParser(queryModel.getDomain());
        this.printTaskTree(queryTask);
    }
    //
    private void printTaskTree(QueryTask queryTask) {
        //
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println(queryTask.printTaskTree(true));
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println(queryTask.printTaskTree(false));
        //
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }
}