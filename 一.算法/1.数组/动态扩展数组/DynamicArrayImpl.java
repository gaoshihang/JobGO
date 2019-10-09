public class DynamicArrayImpl<T> implements DynamicArray<T> {

    private T[] data; //定义一个数组
    private int size; //表示数组大小

    public DynamicArrayImpl(int capacity){
        size = 0;
        data = (T[])new Object[capacity]; //创建数组
    }

    public DynamicArrayImpl(){
        this(10);
    }

    //获取数组容量
    @Override
    public int getCapacity() {
        return data.length;
    }

    //获取数组大小
    @Override
    public int getSize() {
        return size;
    }

    //数组是否为空
    @Override
    public boolean isEmpty() {
        if(size == 0) return true;
        else return false;
    }

    //向数组指定位置添加元素
    @Override
    public void add(int index, T element) {
        //1.判断index是否合法
        if(index < 0 || index > size){
            throw new IllegalArgumentException("index is ellegal");
        }

        //2.判断是否为满
        if(size == data.length){
            resize(size * 2); //扩容操作
        }

        //3.插入操作
        for(int i = size - 1; i >= index; i--){
            data[i+1] = data[i];
        }
      
        //4.进行赋值插入
        data[index] = element;

        //5.元素个数+1
        size++;
    }

    //向数组开头位置添加元素
    @Override
    public void addFirst(T element) {
        if(size == data.length){
            resize(size * 2);
        }

        for(int i = size - 1; i >= 0; i--){
            data[i+1] = data[i];
        }

        data[0] = element;
        size++;
    }

    //向数组尾部插入元素
    @Override
    public void addLast(T element) {
        if(size == data.length){
            resize(size * 2);
        }

        data[size] = element;
        size++;
    }

    //扩容操作
    public void resize(int capacity){
        T[] newData = (T[])new Object[capacity];
        for(int i = 0; i < size; i++){
            newData[i] = data[i];
        }
        data = newData;
    }

    //获取元素
    @Override
    public T get(int index) {
        //1.判断index是否合法
        if(index < 0 || index > size){
            throw new IllegalArgumentException("index is ellegal");
        }
        return data[index];
    }

    //设置元素
    @Override
    public void set(int index, T element) {
        //1.判断index是否合法
        if(index < 0 || index > size){
            throw new IllegalArgumentException("index is ellegal");
        }
        data[index] = element;
    }

    //打印数组
    @Override
    public void print() {
        for(int i = 0; i < size;i++){
            System.out.print(data[i] + " ");
        }
    }

    //查找数组中是否有元素e
    @Override
    public boolean contains(T element) {
        for(int i = 0; i < size; i++){
            if(element.equals(data[i])){
                return true;
            }
        }

        return false;
    }

    //查找数组中元素e所在的索引，如果不存在，返回-1
    public int find(T e){
        for(int i = 0; i < size; i++){
            if(e.equals(data[i])){
                return i;
            }
        }

        return -1;
    }


    //移除某个位置元素
    @Override
    public T remove(int index) {
        //1.判断index是否合法
        if(index < 0 || index > size){
            throw new IllegalArgumentException("index is ellegal");
        }


        T result = data[index];
        for(int i = index; i < size; i++){
            data[i] = data[i+1];
        }


        size--;
        if(size < data.length/4 && data.length/2 != 0){
            resize(data.length/2);
        }
        return result;
    }
}
