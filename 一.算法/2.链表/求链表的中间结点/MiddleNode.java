题目：
给定一个带有头结点 head 的非空单链表，返回链表的中间结点。
如果有两个中间结点，则返回第二个中间结点。

解法：
快慢指针

代码：
public static ListNode middleNode(ListNode head){
  if(head == null) return null;
  
  ListNode slow = head;
  ListNode fast = head;
  while(fast != null && fast.next != null){
    fast = fast.next.next;
    slow = slow.next;
  }
  
  return slow;
}
