package com.gongrui.mta.entity;

import net.tsz.afinal.annotation.sqlite.Table;

@Table(name="t_tempm")
public class Temperature {
	private int id;
	private String gc;
	private String bd;
	private String cw;
	private String wd1;
	private String wd2;
	private String wd3;
	private String hjwd;
	private String hjsd;
	private String wz;
	private String jd;
	private String wd;
	
	private long cwsj;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getGc() {
		return gc;
	}
	public void setGc(String gc) {
		this.gc = gc;
	}
	public String getBd() {
		return bd;
	}
	public void setBd(String bd) {
		this.bd = bd;
	}
	public String getCw() {
		return cw;
	}
	public void setCw(String cw) {
		this.cw = cw;
	}
	public String getWd1() {
		return wd1;
	}
	public void setWd1(String wd1) {
		this.wd1 = wd1;
	}
	public String getWd2() {
		return wd2;
	}
	public void setWd2(String wd2) {
		this.wd2 = wd2;
	}
	public String getWd3() {
		return wd3;
	}
	public void setWd3(String wd3) {
		this.wd3 = wd3;
	}
	public String getHjwd() {
		return hjwd;
	}
	public void setHjwd(String hjwd) {
		this.hjwd = hjwd;
	}
	public String getHjsd() {
		return hjsd;
	}
	public void setHjsd(String hjsd) {
		this.hjsd = hjsd;
	}
	public String getWz() {
		return wz;
	}
	public void setWz(String wz) {
		this.wz = wz;
	}
	public long getCwsj() {
		return cwsj;
	}
	public void setCwsj(long cwsj) {
		this.cwsj = cwsj;
	}
	public String getJd() {
		return jd;
	}
	public void setJd(String jd) {
		this.jd = jd;
	}
	public String getWd() {
		return wd;
	}
	public void setWd(String wd) {
		this.wd = wd;
	}
	

}