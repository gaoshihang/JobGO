单向循环链表与单链表唯一的区别是其尾结点指向的不是null，而是头结点（不是头指针）。
因此，单向链表的任何结点的下一部分都不存在NULL值。

class Node<T>{
    public T data;
    public Node next;
}

public class CircularLink<T> {
    private static Node head = null;

    //初始化
    public void initCircularLink(){
        head = new Node();
        head.data = null;
        head.next = head;
    }

    //插入节点
    public void insertCircularLink(T element){
        Node node = new Node();
        node.data = element;
        if(head.next == head){
            head.next = node;
            node.next = head;
        }else{
            Node tmp = head;
            while(tmp.next != null){
                tmp = tmp.next;
            }
            tmp.next = node;
            node.next = head;
        }
    }

    //值删除
    public boolean deleteCircularLink(T element){
        Node tmp = head;
        if(tmp.next == head){
            System.out.println("链表为空");
            return false;
        }

        while(tmp.next != head){
            if(tmp.next.data == element){
                tmp.next = tmp.next.next;
                return true;
            }else {
                tmp = tmp.next;
            }
        }

        return false;
    }
    
}
