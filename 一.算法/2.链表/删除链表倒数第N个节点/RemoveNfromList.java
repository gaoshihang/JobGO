题目：
给定一个链表，删除链表的倒数第 n 个节点，并且返回链表的头结点。  

代码：
    public static ListNode removeNthFromEnd(ListNode head, int n) {
        if(head == null) return head;
        ListNode slow = head;
        ListNode fast = head;

        for(int i = 0; i < n;i++){
            fast = fast.next;
            if(fast == null) return slow.next;
        }

        while(fast.next != null){
            fast = fast.next;
            slow = slow.next;
        }

        slow.next = slow.next.next;
        return head;
    }
