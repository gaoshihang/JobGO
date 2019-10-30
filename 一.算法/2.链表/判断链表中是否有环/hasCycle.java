题目：
给定一个链表，判断链表中是否有环。

题解：
快慢指针，如果相遇，则有环；如果快指针到了null，则没有环。

复杂度分析：
时间复杂度O(n)
空间复杂度O(1)

代码：
public boolean hasCycle(ListNode head) {
    if (head == null || head.next == null) {
        return false;
    }
    ListNode slow = head;
    ListNode fast = head.next;
    while (slow != fast) {
        if (fast == null || fast.next == null) {
            return false;
        }
        slow = slow.next;
        fast = fast.next.next;
    }
    return true;
}


升级版题目：
给定一个链表，返回链表开始入环的第一个节点。 如果链表无环，则返回 null。

题解：
双指针法。构建两次相遇。
详细题解：https://leetcode-cn.com/problems/linked-list-cycle-ii/solution/linked-list-cycle-ii-kuai-man-zhi-zhen-shuang-zhi-/  

代码：
    public ListNode detectCycle(ListNode head) {
        ListNode fast = head;
        ListNode slow = head;
        while(true){
            if(fast == null || fast.next == null) return null;
            fast = fast.next.next;
            slow = slow.next;
            if(fast == slow) break;
        }

        fast = head;
        while(slow != fast){
            slow = slow.next;
            fast = fast.next;
        }

        return fast;
    }
