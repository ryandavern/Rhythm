package com.beatplaylist.enums;

public enum SizeType {

	// Class written by Taylor. - Wednesday 20th 2016 (April) (20/04/2016. Approx time 10.00 - 11.00 p.m)

	// Stable Sizes
	// SIZE_TYPE1620X720

	// All sizes must be stable before release.
	// SIZE_LOGIN(0, 480, 550), Login Old
	// SIZE_LOGIN(0, 600, 550), //

	SIZE_LOGIN(0, 600, 550), //
	SIZE_TYPE2920X1080(9, 2920, 1080), //
	SIZE_TYPE1920X1080(8, 1920, 1080), //
	SIZE_TYPE1620X720(6, 1620, 780), //
	SIZE_TYPE1280X720(5, 1280, 720), //
	SIZE_TYPE1280X620(4, 1280, 620), //
	SIZE_TYPE1200X720(3, 1200, 720), //
	SIZE_TYPE1200X620(2, 1200, 620), //
	SIZE_TYPESMALLEST(1, 1000, 500);

	private int width, height, id;

	SizeType(int id, int width, int height) {
		this.id = id;
		this.width = width;
		this.height = height;
	}

	public void setWidth(int value) {
		this.width = value;
	}

	public void setHeight(int value) {
		this.height = value;
	}

	public int getId() {
		return this.id;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}
}