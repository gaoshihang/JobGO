一.题目：将两个有序链表合并
代码如下：
public static ListNode mergeTwoLists(ListNode l1, ListNode l2){
    ListNode prehead = new ListNode(-1);

    ListNode prev = prehead;
    while(l1 != null && l2 != null){
        if(l1.val <= l2.val){
            prev.next = l1;
            l1 = l1.next;
        }else{
            prev.next = l2;
            l2 = l2.next;
        }
        prev = prev.next;
    }

    prev.next = l1 == null ? l2 : l1;
    return prehead.next;
}

二.进阶版题目：合并k个有序链表
1.解法一：优先队列
与合并两个相同，找出k个链表表头最小的元素，放在新链表的下一个位置。
找最小的这件事，可以使用优先队列（常见的有最小堆）来完成。
    public static ListNode mergeKList(ListNode[] lists){
        int len = lists.length;
        if(len == 0){
            return null;
        }

        PriorityQueue<ListNode> queue = new PriorityQueue<>(len, Comparator.comparingInt(value -> value.val));
        ListNode dummyNode = new ListNode(-1);
        ListNode curNode = dummyNode;
        for(ListNode node : lists){
            //不要把null添加进队列
            if(node != null){
                queue.add(node);
            }
        }

        while(!queue.isEmpty()){
            //队列非空才能出队
            ListNode nextNode = queue.poll();
            curNode.next = nextNode;
            //curNode指向刚刚出队的元素
            curNode = curNode.next;
            if(curNode.next != null){
                //只有非空结点才能加入优先队列
                queue.add(curNode.next);
            }
        }

        return dummyNode.next;
    }

