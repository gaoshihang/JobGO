题目：  
编写一个程序，找到两个单链表相交的起始节点。  

代码如下：  
```
        if(headA == null || headB == null) return null;

        int lengthA = 0;
        int lengthB = 0;
        ListNode curNode = headA;
        while(curNode != null){
            lengthA++;
            curNode = curNode.next;
        }

        curNode = headB;
        while(curNode != null){
            lengthB++;
            curNode = curNode.next;
        }

        ListNode curA = headA;
        ListNode curB = headB;
        if(lengthA > lengthB){
            int gap = lengthA - lengthB;
            for(int i = 0; i < gap;i++){
                curA = curA.next;
            }
        }else if(lengthA < lengthB){
            int gap = lengthB - lengthA;
            for(int i = 0; i < gap;i++){
                curB = curB.next;
            }
        }

        while(curA != null || curB != null){
            if(curA == curB) return curA;
            curA = curA.next;
            curB = curB.next;
        }

        return null;
```
