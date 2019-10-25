题目：给定一个非空整数数组，除了某个元素只出现一次以外，其余每个元素均出现两次。找出那个只出现了一次的元素。

1.解法一：利用哈希表
代码略

2.解法二：利用异或运算
概念：
（1）对0和二进制a做异或（XOR），其结果还是a；
（2）对相同二进制a做异或，其结果为0；
（3）异或满足交换律；

代码如下：
public static int singleNumber(int[] nums){
  if(nums == null) return Integer.MIN_VALUE;
  
  int number = nums[0];
  for(int i = 1; i < nums.length;i++){
    number = number ^ nums[i];
  }
  
  return number;
}
