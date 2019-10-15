package com.liebrother.thread;

import java.util.Arrays;

/**
 * @author James
 * @date 10/15/2019
 */
public class TestSort {


    public static void main(String[] args) {
        int[] array = {5,1,9,3,2,6,4,7,8};
        print(array);
        array = mergeSort(array);
        print(array);
    }

    private static void print(int[] array) {
        System.out.println();
        for (int a : array) {
            System.out.print(a + ",");
        }
    }

    /**
     * 冒泡排序
     * 时间：最好O(n) 最坏O(n2) 平均O(n2)
     * 空间：O(1)
     *
     * 每一次对比现存相邻数据大小，把最大的往后移
     */
    public static int[] bubbleSort(int[] array) {
        for (int i = 0; i < array.length; i ++) {
            for (int j = 0; j < array.length - i - 1; j++) {
                // 相邻 2 个数对比，大的放后面
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
        return array;
    }

    /**
     * 选择排序
     * 时间：最好O(n2) 最坏O(n2) 平均O(n2)
     * 空间：O(1)
     *
     * 每一次找出现存最小的数据的下标，然后将这个下标的数据放在对的位置
     */
    public static int[] selectionSort(int[] array) {

        for (int i = 0; i < array.length; i++) {
            int minIndex = i;
            for (int j = i; j < array.length; j ++) {
                // 找出最小数据的下标
                if (array[j] < array[minIndex]) {
                    minIndex = j;
                }
            }

            if (minIndex != i) {
                // 交换数据
                int temp = array[minIndex];
                array[minIndex] = array[i];
                array[i] = temp;
            }
        }
        return array;
    }

    /**
     * 插入排序
     * 时间：最好O(n) 最坏O(n2) 平均O(n2)
     * 空间：O(0)
     *
     * 每一次找出现存最小的数据的下标，然后将这个下标的数据放在对的位置
     */
    public static int[] insertionSort(int[] array) {
        for (int i = 1; i < array.length; i++) {
            // 当前需要比较的数
            int current = array[i];
            int preIndex = i - 1;
            while (preIndex >= 0 && array[preIndex] > current) {
                // 从 i 找到 0，找到 current 要存放的位置，往后移
                array[preIndex + 1] = array[preIndex];
                preIndex --;
            }
            // current 存放的位置
            array[preIndex + 1] = current;
        }
        return array;
    }


    /**
     * 希尔排序
     * 时间：最好O(nlog2n) 最坏O(nlog2n) 平均O(nlog2n)
     * 空间：O(1)
     */
    public static int[] shellSort(int[] array) {
        int len = array.length;
        int gap = len / 2;
        while (gap > 0) {
            for (int i = gap; i < len; i++) {
                int current = array[i];
                int preIndex = i - gap;
                while (preIndex >= 0 && array[preIndex] > current) {
                    array[preIndex + gap] = array[preIndex];
                    preIndex -= gap;
                }
                array[preIndex + gap] = current;
            }
            gap /= 2;
        }

        return array;
    }


    public static int[] mergeSort(int[] array) {
        if (array.length < 2) {
            return array;
        }
        int mid = array.length / 2;
        int[] left = Arrays.copyOfRange(array, 0, mid);
        int[] right = Arrays.copyOfRange(array, mid, array.length);
        return merge(mergeSort(left), mergeSort(right));
    }

    private static int[] merge(int[] left, int[] right) {
        int[] result = new int[left.length + right.length];
        int i = 0, j = 0;
        int resultIndex = 0;
        while (i < left.length && j < right.length) {
            if (left[i] <= right[j]) {
                result[resultIndex++] = left[i++];
            } else {
                result[resultIndex++] = right[j++];
            }
        }
        while (i < left.length) {
            result[resultIndex++] = left[i++];
        }
        while (j < right.length) {
            result[resultIndex++] = right[j++];
        }
        return result;
    }

}
/*
public static int[] ShellSort(int[] array) {
    int len = array.length;
    int temp, gap = len / 2;
    while (gap > 0) {
        for (int i = gap; i < len; i++) {
            temp = array[i];
            int preIndex = i - gap;
            while (preIndex >= 0 && array[preIndex] > temp) {
                array[preIndex + gap] = array[preIndex];
                preIndex -= gap;
            }
            array[preIndex + gap] = temp;
        }
        gap /= 2;
    }
    return array;
}
 */
