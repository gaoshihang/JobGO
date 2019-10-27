1.迭代
解法：在遍历列表时，将当前节点的 next 指针改为指向前一个元素。由于节点没有引用其上一个节点，因此必须事先存储其前一个元素。
在更改引用之前，还需要另一个指针来存储下一个节点。不要忘记在最后返回新的头引用！

    //迭代版
    public ListNode reverseList(ListNode head) {
        if(head == null || head.next == null) return head;

        ListNode preNode = null;
        ListNode curNode = head;
        while(curNode != null){
            ListNode nextNode = curNode.next;
            curNode.next = preNode;
            preNode = curNode;
            curNode = nextNode;
        }
        return preNode;
    }

二.升级版（反转m到n之间的结点）
题目：反转从位置 m 到 n 的链表。请使用一趟扫描完成反转。

解法：https://leetcode-cn.com/problems/reverse-linked-list-ii/solution/fan-zhuan-lian-biao-ii-by-leetcode/
代码如下：
    //反转位置从m到n的元素，使用一趟遍历
    public static ListNode reverseBetween(ListNode head, int m, int n) {
        if(head == null || head.next == null) return head;

        int count = 1;
        ListNode con = null;
        ListNode tail = null;
        ListNode preNode = null;
        ListNode curNode = head;
        while(curNode != null){
            if(m == 1){
                con = null;
                tail = head;
            }else{
                if(count == m - 1){
                    con = curNode;
                    tail = curNode.next;
                }
            }

            if(count > m && count <= n){
                ListNode nextNode = curNode.next;
                curNode.next = preNode;
                preNode = curNode;
                curNode = nextNode;
            }else{
                if(count > n) break;
                preNode = curNode;
                curNode = curNode.next;
            }

            count++;
        }

        if(con != null){
            con.next = preNode;
        }else{
            head = preNode;
        }

        tail.next = curNode;

        return head;
    }
