题目：
已知sqrt(2)约等于1.414，要求不用数学库，求sqrt(2)精确到小数点后10位

解法：
可以使用二分法。已知sqrt(2)等于1.414，那么就可以在(1.4,1.5)区间做二分查找如下：
1) high = 1.5
2) low = 1.4
3) mid = (low + high)/2 = 1.45
4) 1.45 * 1.45 > 2 ? high = 1.45 : low = 1.45

退出条件：前后两次的差值的绝对值<=0.0000000001，即可退出

    public static double sqrt2(){
        double EPSINON = 0.0000000001;
        double low = 1.4;
        double high = 1.5;

        double mid = (low + high)/2;

        while(high - low > EPSINON){
            if(mid * mid > 2){
                high = mid;
            }else {
                low = mid;
            }

            mid = (low + high)/2;

        }

        return mid;
    }
