题目:
给定一个整数数组，你需要寻找一个连续的子数组，如果对这个子数组进行升序排序，那么整个数组都会变为升序排序。
你找到的子数组应是最短的，请输出它的长度。

1.解法一：暴力破解法
考虑nums数组中每一个可能的子序列，对每个子序列nums[i:j]，求其最大值max和最小值min。
如果子序列nums[0:i-1]和nums[j:n-1]是升序的，那么仅有nums[i:j]是可能的子序列。
更进一步，nums[0:i-1]中所有元素都要比min小且nums[j:n-1]中所有元素都要比max大。对于枚举的每一对i和j都要做这样的检查。
接下来，检查nums[0:i-1]和nums[j:n-1]是否是升序的。如果上述所有条件都满足，则通过枚举所有的i和j并计算j-i来找到最短无序子数组。  

时间复杂度O(n^3)，空间O(1)，代码如下：
    public static int findUnsortedSubarray(int[] nums){
        int res = nums.length;
        for(int i = 0; i < nums.length;i++){
            for(int j = i+1; j < nums.length;j++){
                int min = Integer.MAX_VALUE;
                int max = Integer.MIN_VALUE;
                int prev = Integer.MIN_VALUE;
                for(int k = i; k < j;k++){
                    min = Math.min(nums[k], min);
                    max = Math.max(nums[k], max);
                }
                if((i > 0 && nums[i-1] > min) || (j < nums.length && nums[j] < max)){
                    continue;
                }

                int k = 0;
                while(k < i && prev <= nums[k]){
                    prev = nums[k];
                    k++;
                }
                if(k != i) continue;

                k = j;
                while(k < nums.length && prev <= nums[k]){
                    prev = nums[k];
                    k++;
                }
                if(k == nums.length){
                    res = Math.min(res, j-i);
                }
            }
        }
        return res;
    }
    
2.解法二：更好的暴力解法
解法：我们基于选择排序使用如下想法：遍历数组中每一个nums[i]，在i<j<n，n为数组长度的区间里，用nums[j]和nums[i]做比较。
若nums[j] < nums[i]，则i和j上的数字都不在正确的位置，这两个元素标记着当前无序数组的边界。
遍历所有的nums[i]，找到最左边不正确位置的nums[i]，这标记着最左边界nums[l]；找到最右边不正确位置的nums[j]，标记着最右边界r。
时间复杂度：O(n^2)

代码如下：
public static int findUnsortedSubarray(int[] nums){
    int l = nums.length;
    int r = 0;
    for(int i = 0; i < nums.length-1; i++){
        for(int j = i+1; j < nums.length; j++){
            if(nums[j] < nums[i]){
                l = Math.min(l, i);
                r = Math.min(r, j);
            }
        }
    }
    return r - 1 < 0 ? 0 : r - l + 1;
}

3.解法三：排序
先对原始的数组进行排序，排序后与原数组进行比较，得出最左边和最右边的不匹配元素。
时间复杂度：O(nlogn)，排序
空间复杂度：O(n)，拷贝原数组
代码如下：
public static int findUnsortedSubarray(int[] nums){
    int[] snums = nums.clone();
    Arrays.sort(snums);
    int start = snums.length;
    int end = 0;
    for(int i = 0; i < snums.length; i++){
        if(nums[i] != snums[i]){
            start = Math.min(start, i);
            end = Math.max(end, i);
        }
    }
    
    return start < end ? end - start + 1 : 0;
}

4.解法四：使用栈
从头遍历数组nums，如果一直是升序的，则将其下标压入栈中，直到遇到一个降序的数字nums[j]，不断的从栈顶弹出元素，直到栈顶数组大小小于这个数字，
若此时栈顶元素的下标为k，则得到nums[j]应该在的下标位置，即为k+1。
我们重复这一过程并遍历完整个数组，这样我们可以找到最小的 k， 它也是无序子数组的左边界。
类似的，我们逆序遍历一遍 numsnums 数组来找到无序子数组的右边界。
这一次我们将降序的元素压入栈中，如果遇到一个升序的元素，我们像上面所述的方法一样不断将栈顶元素弹出，直到找到一个更大的元素，
以此找到无序子数组的右边界。
时间复杂度：O(n)
空间复杂度：O(n)

代码如下：
public static int findUnsortedSubarray(int[] nums){
    Stack<Integer> stack = new Stack<>();
    int l = nums.length;
    int r = 0;
    for(int i = 0; i < nums.length; i++){
        while(!stack.isEmpty() && nums[stack.peek()] > nums[i]){
            l = Math.min(l, stack.pop());
        }
        stack.push(i);
    }
    
    stack.clear();
    for(int i = 0; i < nums.length; i++){
        while(!stack.isEmpty() && nums[stack.peek()] < nums[i]){
            r = Math.max(r, stack.pop());
        }
        stack.push(i);
    }
    
    return r - 1 > 0 ? r - l + 1 : 0;
}














