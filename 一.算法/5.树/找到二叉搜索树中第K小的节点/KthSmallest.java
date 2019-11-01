解法：
树相关的题目，一般都可用递归求解，左右子树分别遍历。
对于二叉搜索树，root大于左子树，且小于右子树。那么有下面三种情况：
（1）左子树的节点数目等于K-1，那么root就是结果
（2）如果左子树的节点数目小于K-1，那么结果在右子树
（3）如果左子树节点数目大于K-1.那么结果在左子树。
因此，在搜索的时候同时返回节点数目，跟K做对比，就能得出结果。  

代码：
    private class ResultType{
        boolean found;  //是否找到
        int val;    //节点数目
        ResultType(boolean found, int val){
            this.found = found;
            this.val = val;
        }
    }

    public int kthSmallest(TreeNode root, int k){
        return kthSmallestHelper(root, k).val;
    }

    public ResultType kthSmallestHelper(TreeNode root, int k){
        if(root == null) return new ResultType(false, 0);

        ResultType left = kthSmallestHelper(root.left, k);
        //左子树找到，直接返回
        if(left.found){
            return new ResultType(true, left.val);
        }

        //左子树的节点数目 = k - 1,结果为root
        if(k - left.val == 1){
            return new ResultType(true, root.val);
        }

        //右子树寻找
        ResultType right = kthSmallestHelper(root.right, k - left.val - 1);
        if(right.found){
            return new ResultType(true, right.val);
        }

        return new ResultType(false, left.val + 1 + right.val);
    }
