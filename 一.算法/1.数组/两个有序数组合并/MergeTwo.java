解法：从后向前数组遍历。一边遍历一边将值填充进去。
    设置指针len1和len2分别指向nums1和nums2的有数字尾部，从尾部值开始比较遍历，同时设置指针len指向nums1最末尾，每次遍历比较值大小后，则进行填充。
    当len1<0时遍历技术，此时nums2中还有数据未拷贝完全，将其直接拷贝到nums1的前面。

    时间复杂度：O(m+n)。
    空间复杂度：0。

//将nums2合并进入nums1中
public static void merge(int[] nums1, int m, int[] nums2, int n) {
    int len1 = m - 1;
    int len2 = n - 1;
    int len = m + n - 1;
    while(len1 >= 0 && len2 >= 0){
        if(nums1[len1] > nums2[len2]){
            nums1[len] = nums1[len1];
            len1--;
        }else{
            nums1[len] = nums2[len2];
            len2--;
        }
        len--;
    }


    System.arraycopy(nums2, 0, nums1, 0, len2+1);
}
