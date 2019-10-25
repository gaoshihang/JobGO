题目：输入一个整型数组，数组里有正数也有负数。数组中一个或连续的多个整数组成一个子数组。求所有子数组的和的最大值。要求时间复杂度为O(n)。
例子：例如输入的数组为{1, -2, 3, 10, -4, 7, 2, -5}，和最大的子数组为3, 10, -4, 7, 2}。因此输出为该子数组的和18。

解法：动态规划
我们令curSum为当前最大子数组的和，maxSum为最后要返回的最大子数组的和。判断以下两种情况：
1.（要继承前人遗产吗？）往后扫描时，对第j个元素有以下两种选择：要么放入前面找到的子数组，要么作为新子数组的第一个元素。
（1）若curSum + a[j] >= a[j]，则将curSum加上a[j]；
（2）否则，curSum重新赋值，置为下一个元素，即curSum = a[j];
2.（当前值是否最大？）比较当前子数组和与最大子数组的和。
（1）当curSum > maxSum，则更新maxSum = curSum；
（2）否则保持原值，不更新

动态规划公式如下：
curSum = max(a[j], curSum + a[j])
maxSum = max(curSum, maxSum)


public static int maxSubArray(int[] arr){
  if(arr.length == 0) return Integer.MIN_VALUE;
  
  int curSum = arr[0];
  int maxSum = curSum;
  for(int i = 1; i < arr.length; i++){
    if(curSum + arr[i] >= arr[i]){
      curSum = curSum + arr[i];
    }else{
      curSum = arr[i];
    }
    
    if(curSum > maxSum){
      maxSum = curSum;
    }
  }
  
  return maxSum;
}
