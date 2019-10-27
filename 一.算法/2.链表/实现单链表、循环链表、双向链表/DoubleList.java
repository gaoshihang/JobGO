class DoubleNode{
    public int value;
    public DoubleNode pre;
    public DoubleNode next;

    public DoubleNode(int data){
        this.value = data;
        this.pre = null;
        this.next = null;
    }

}

//双向链表
public class DoubleList {

    public static DoubleNode head = null;
    public static int length = 0;

    //在链表头部添加结点
    public static void addHead(int data){
        DoubleNode node = new DoubleNode(data);
        if(head == null){
            head = node;
            return;
        }else{
            node.next = head;
            head.pre = node;
            head = node;
        }
        length++;
    }

    //在链表头部删除结点
    public static void deleteHead(){
        if(head == null) return;
        DoubleNode node = head;
        head = node.next;
        head.pre = null;
        length--;
    }

    //在链表尾部增加结点
    public static void addTail(int data){
        DoubleNode node = new DoubleNode(data);
        if(head == null){
            head = node;
            return;
        }else{
            DoubleNode curNode = head;
            while(curNode.next != null){
                curNode = curNode.next;
            }
            curNode.next = node;
            node.pre = curNode;
        }
        length++;
    }

    //在链表尾部删除结点
    public static void deleteTail(){
        if(head == null){
            System.out.println("链表为空");
            return;
        }else{
            DoubleNode curNode = head;
            while(curNode.next != null){
                curNode = curNode.next;
            }
            DoubleNode preNode = curNode.pre;
            preNode.next = null;
        }
        length--;
    }

    //在指定位置插入结点
    public static void insertList(int data, int index){
        DoubleNode node = new DoubleNode(data);
        if(head == null){
            head = node;
            return;
        }

        if(index > length+1 || index < 1){
            System.out.println("插入位置不合法");
        }

        if(index == 1){
            node.next = head;
            head.pre = node;
            head = node;
        }else{
            int count = 1;
            DoubleNode preNode = head;
            while(count < index - 1){
                preNode = preNode.next;
                count++;
            }

            DoubleNode curNode = preNode.next;
            node.next = curNode;
            node.pre = preNode;
            preNode.next = node;
            if(curNode != null){
                curNode.pre = node;
            }
        }
        length++;
    }

    //在指定位置删除链表
    public static void deleteList(int index){
        if(head == null){
            System.out.println("链表为空");
            return;
        }

        if(index > length + 1 || index < 1){
            System.out.println("删除位置不合法");
            return;
        }

        if(index == 1){
            head = head.next;
            head.pre = null;
        }else{
            DoubleNode preNode = head;
            int count = 1;
            while(count < index - 1){
                preNode = preNode.next;
                count++;
            }
            DoubleNode curNode = preNode.next;
            DoubleNode nextNode = curNode.next;
            preNode.next = nextNode;
            if(nextNode != null){
                nextNode.pre = preNode;
            }
        }
        length--;
    }

}
