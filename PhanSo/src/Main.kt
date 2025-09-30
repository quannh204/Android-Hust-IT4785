import java.util.Scanner
import kotlin.math.abs

class PhanSo(var tu: Int, var mau: Int) {

    // Nhap phan so
    fun nhap(scanner: Scanner) {
        while (true) {
            print("Nhập tử số: ")
            tu = scanner.nextInt()
            print("Nhập mẫu số: ")
            mau = scanner.nextInt()
            if (tu != 0 && mau != 0) break
            println("Tử số và mẫu số phải khác 0. Vui lòng nhập lại!")
        }
    }

    // Xuat phan so
    fun xuat() {
        println("$tu/$mau")
    }

    // Rut gon phan so
    fun toiGian() {
        val ucln = ucln(abs(tu), abs(mau))
        tu /= ucln
        mau /= ucln
        if (mau < 0) { // chuẩn hoá mẫu dương
            tu = -tu
            mau = -mau
        }
    }

    private fun ucln(a: Int, b: Int): Int {
        return if (b == 0) a else ucln(b, a % b)
    }

    // So sanh phan so
    fun soSanh(ps: PhanSo): Int {
        val left = this.tu * ps.mau
        val right = ps.tu * this.mau
        return when {
            left < right -> -1
            left > right -> 1
            else -> 0
        }
    }

    // Cong phan so
    fun cong(ps: PhanSo): PhanSo {
        val tuMoi = this.tu * ps.mau + ps.tu * this.mau
        val mauMoi = this.mau * ps.mau
        val kq = PhanSo(tuMoi, mauMoi)
        kq.toiGian()
        return kq
    }
}

fun main() {
    val scanner = Scanner(System.`in`)
    print("Nhập số lượng phân số: ")
    val n = scanner.nextInt()
    val arr = Array(n) { PhanSo(0, 1) }

    // Nhap mang
    for (i in arr.indices) {
        println("Nhập phân số thứ ${i + 1}: ")
        arr[i].nhap(scanner)
    }

    // In mang
    println("Mảng phân số vừa nhập là: ")
    arr.forEach { it.xuat() }

    // Toi gian
    println("Mảng phân số sau khi tối giản là: ")
    arr.forEach {
        it.toiGian()
        it.xuat()
    }

    // Tinh tong
    var tong = PhanSo(0, 1)
    for (ps in arr) {
        tong = tong.cong(ps)
    }
    println("Tổng các phân số là: ")
    tong.xuat()

    // Phan so lon nhat
    var maxPS = arr[0]
    for (ps in arr) {
        if (ps.soSanh(maxPS) > 0) {
            maxPS = ps
        }
    }
    println("Phân số lớn nhất là: ")
    maxPS.xuat()

    // Sap xep giam dan
    arr.sortWith { a, b -> b.soSanh(a) }
    println("Mảng phân số được sắp xếp giảm dần là: ")
    arr.forEach { it.xuat() }
}