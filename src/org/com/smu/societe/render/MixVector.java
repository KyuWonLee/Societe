package org.com.smu.societe.render;

/**
 * MixVector Ŭ������ ���Ϳ� ���� Ŭ������. ���Ϳ� ���õ� ���� ������ ������ ���� ���̺귯����.
 * ������ ���� ������ ���п����� ���� ������ �⺻���� �� ����
 * 
 * @Project : Campus AR 
 * @File Name : MixVector.java
 * @Date : 2010-10-10
 * @author App+
 *
 */
public class MixVector {
	public float x;
	public float y;
	public float z;

	/**
	 * 0�� ������ �ʱ�ȭ�Ͽ� Vector����
	 */
	public MixVector() {
		this(0, 0, 0);
	}

	/**
	 * ���޹��� vector ������ ����
	 * @param v	MixVector
	 */
	public MixVector(MixVector v) {
		this(v.x, v.y, v.z);
	}

	/**
	 * ���޹��� �迭������ ���� ����
	 * @param v	float[]
	 */
	public MixVector(float v[]) {
		this(v[0], v[1], v[2]);
	}

	/**
	 * x, y, z ��ǥ�� ������ ���� ����
	 * 
	 * @param x		x��ǥ
	 * @param y		y��ǥ
	 * @param z		z��ǥ
	 */
	public MixVector(float x, float y, float z) {
		set(x, y, z);
	}

	@Override
	public boolean equals(Object obj) {
		//���Ͱ� ������ ���ϴ� �Լ�
		MixVector v = (MixVector) obj;
		return (v.x == x && v.y == y && v.z == z);
	}

	/**
	 * ������ equal����
	 * 
	 * @param x	x
	 * @param y	y
	 * @param z	z
	 * @return
	 */
	public boolean equals(float x, float y, float z) {
		return (this.x == x && this.y == y && this.z == z);
	}

	@Override
	public String toString() {
		//������ x, y, z ���ڵ��� ���
		return "<" + x + ", " + y + ", " + z + ">";
	}

	/**
	 * ���޹��� Vector�� ����
	 * 
	 * @param v	MixVector
	 */
	public void set(MixVector v) {
		set(v.x, v.y, v.z);
	}

	/**
	 * ���޹��� x, y, z���� ������
	 * 
	 * @param x	x
	 * @param y	y
	 * @param z	z
	 */
	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * ������ �� ���� �Լ�
	 * 
	 * @param x	x
	 * @param y y
	 * @param z	z
	 */
	public void add(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
	}

	/**
	 * ������ �� �����Լ�
	 * 
	 * @param v	MixVector
	 */
	public void add(MixVector v) {
		add(v.x, v.y, v.z);
	}

	/**
	 * ������ ���� �����Լ�
	 * 
	 * @param x x
	 * @param y y
	 * @param z z
	 */
	public void sub(float x, float y, float z) {
		add(-x, -y, -z);
	}

	/**
	 * ������ ���� �����Լ�
	 * 
	 * @param v	MixVector
	 */
	public void sub(MixVector v) {
		add(-v.x, -v.y, -v.z);
	}

	/**
	 * ����dp ��Į����� ���ִ� �Լ�
	 * 
	 * @param s	float
	 */
	public void mult(float s) {
		x *= s;
		y *= s;
		z *= s;
	}

	/**
	 * �������� ���ִ� �Լ�
	 * 
	 * @param s
	 */
	public void divide(float s) {
		x /= s;
		y /= s;
		z /= s;
	}

	/**
	 * ������ ���̸� ���ϴ� �Լ�
	 * 
	 * @return	������ ����
	 */
	public float length() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	/**
	 * normal���� �����ϴ� �Լ�
	 */
	public void norm() {
		divide(length());
	}

	/**
	 * ������ ������ ���ϴ� �Լ�
	 * 
	 * @param v	MixVector
	 * @return	�������� ��
	 */
	public float dot(MixVector v) {
		return x * v.x + y * v.y + z * v.z;
	}

	/**
	 * ������ �������� �Լ�
	 * 
	 * @param u	MixVector
	 * @param v	MixVector
	 */
	public void cross(MixVector u, MixVector v) {
		float x = u.y * v.z - u.z * v.y;
		float y = u.z * v.x - u.x * v.z;
		float z = u.x * v.y - u.y * v.x;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * ���Ϳ� ����� ��������
	 * 
	 * @param m	Matrix
	 */
	public void prod(Matrix m) {
		float xTemp = m.a1 * x + m.a2 * y + m.a3 * z;
		float yTemp = m.b1 * x + m.b2 * y + m.b3 * z;
		float zTemp = m.c1 * x + m.c2 * y + m.c3 * z;

		x = xTemp;
		y = yTemp;
		z = zTemp;
	}
}
