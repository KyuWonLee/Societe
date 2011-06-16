package org.com.smu.societe.render;

/**
 * MixVector 클래스는 벡터에 관한 클래스임. 벡터에 관련된 여러 연산을 정의해 놓은 라이브러리임.
 * 각각의 벡터 연산은 수학에서의 벡터 연산을 기본으로 한 것임
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
	 * 0의 값으로 초기화하여 Vector생성
	 */
	public MixVector() {
		this(0, 0, 0);
	}

	/**
	 * 전달받은 vector 값으로 생성
	 * @param v	MixVector
	 */
	public MixVector(MixVector v) {
		this(v.x, v.y, v.z);
	}

	/**
	 * 전달받은 배열값으로 벡터 생성
	 * @param v	float[]
	 */
	public MixVector(float v[]) {
		this(v[0], v[1], v[2]);
	}

	/**
	 * x, y, z 좌표를 가지고 벡터 생성
	 * 
	 * @param x		x좌표
	 * @param y		y자표
	 * @param z		z좌표
	 */
	public MixVector(float x, float y, float z) {
		set(x, y, z);
	}

	@Override
	public boolean equals(Object obj) {
		//벡터가 같은지 비교하는 함수
		MixVector v = (MixVector) obj;
		return (v.x == x && v.y == y && v.z == z);
	}

	/**
	 * 벡터의 equal연산
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
		//벡터의 x, y, z 인자들을 출력
		return "<" + x + ", " + y + ", " + z + ">";
	}

	/**
	 * 전달받은 Vector를 세팅
	 * 
	 * @param v	MixVector
	 */
	public void set(MixVector v) {
		set(v.x, v.y, v.z);
	}

	/**
	 * 전달받은 x, y, z값을 세팅함
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
	 * 벡터의 합 연산 함수
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
	 * 벡터의 합 연산함수
	 * 
	 * @param v	MixVector
	 */
	public void add(MixVector v) {
		add(v.x, v.y, v.z);
	}

	/**
	 * 벡터의 뺄셈 연산함수
	 * 
	 * @param x x
	 * @param y y
	 * @param z z
	 */
	public void sub(float x, float y, float z) {
		add(-x, -y, -z);
	}

	/**
	 * 벡터의 빼기 연산함수
	 * 
	 * @param v	MixVector
	 */
	public void sub(MixVector v) {
		add(-v.x, -v.y, -v.z);
	}

	/**
	 * 벡터dp 스칼라곱을 해주는 함수
	 * 
	 * @param s	float
	 */
	public void mult(float s) {
		x *= s;
		y *= s;
		z *= s;
	}

	/**
	 * 나눗셈을 해주는 함수
	 * 
	 * @param s
	 */
	public void divide(float s) {
		x /= s;
		y /= s;
		z /= s;
	}

	/**
	 * 벡터의 길이를 구하는 함수
	 * 
	 * @return	벡터의 길이
	 */
	public float length() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	/**
	 * normal벡터 생성하는 함수
	 */
	public void norm() {
		divide(length());
	}

	/**
	 * 벡터의 내적을 구하는 함수
	 * 
	 * @param v	MixVector
	 * @return	내적계산된 값
	 */
	public float dot(MixVector v) {
		return x * v.x + y * v.y + z * v.z;
	}

	/**
	 * 벡터의 외적연산 함수
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
	 * 벡터와 행렬의 곱셈연산
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
