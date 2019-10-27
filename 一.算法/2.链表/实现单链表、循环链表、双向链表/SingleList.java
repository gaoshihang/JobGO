单链表的头结点不存储任何数据，只是充当一个指向链表中真正存放数据的第一个节点的作用，而每个节点中都有一个next引用，指向下一个节点，
就这样一节一节往下面记录，直到最后一个节点，其中的next指向null。

public class ListTest {

    public class ListNode{
        Integer data;
        ListNode next;

        ListNode(){
            data = null;
        }

        ListNode(int data){
            this.data = data;
        }
    }

    private ListNode head; //头结点
    private ListNode temp; //临时节点

    //初始化链表，生成一个无数据的头结点
    ListTest(){
        head = new ListNode();
    }

    //增加节点
    public void addNode(int data){
        ListNode node = new ListNode(data);
        temp = head;
        while(temp.next != null){
            temp = temp.next;
        }

        temp.next = node;
    }

    //返回链表长度
    public int getLength(){
        temp = head;
        int length = 0;
        while(temp.next != null){
            temp = temp.next;
            length++;
        }
        return length;
    }

    //增加节点到链表指定位置
    public void addNodeByIndex(int data, int index){
        if(index < 1 || index > getLength() + 1){
            System.out.println("插入位置不合法");
            return;
        }

        int count = 1; //记录遍历位置
        ListNode node = new ListNode(data);
        temp = head;
        while(temp.next != null){
            if(index == count++){
                node.next = temp.next;
                temp.next = node;
                return;
            }
            temp = temp.next;
        }
    }

    //删除指定位置节点
    public void deleteByIndex(int index){
        if(index < 1 || index > getLength() + 1){
            System.out.println("删除位置不合法");
            return;
        }

        int count = 1; //记录位置
        temp = head;
        while(temp.next != null){
            if(index == count++){
                temp.next = temp.next.next;
                return;
            }
            temp = temp.next;
        }
    }

}
