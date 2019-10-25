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
