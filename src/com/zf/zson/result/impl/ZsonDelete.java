package com.zf.zson.result.impl;

import java.util.ArrayList;
import java.util.List;

import com.zf.zson.ZsonUtils;
import com.zf.zson.object.ZsonObject;
import com.zf.zson.path.ZsonPath;
import com.zf.zson.result.ZsonAction;
import com.zf.zson.result.ZsonResult;

public class ZsonDelete implements ZsonAction{
	
	@Override
	public void process(ZsonResult zr, Object value, String currentPath) {
		ZsonResultImpl zri = (ZsonResultImpl) zr;
		String key = zri.getElementKey(value);
		String parentPath = null;
		if(key!=null){
			parentPath = this.getParentPath(currentPath);
		}else{
			//parentPath = 
		}
	}
	
/*	if(key!=null){
		int deleteIndex = this.getKeyIndexInLevel(zri, key);
		this.deleteCollection(zri, currentPath, deleteIndex, key);
		zri.getzResultInfo().getIndex().remove(key);
		zri.getzResultInfo().getLevel().remove(deleteIndex);
		this.deletePath(zri, currentPath, deleteIndex);
	}else{
		String pkey = this.getKeyByPath(zri, currentPath);
		int deleteIndex = this.getKeyIndexInLevel(zri, pkey);
		ZsonPath zp = this.deletePath(zri, currentPath, deleteIndex);
		ZsonObject<Object> pObj = new ZsonObject<Object>();
		pObj.objectConvert(zri.getzResultInfo().getCollections().get(deleteIndex));
		if(zp.getIndex()!=null){
			if(pObj.isList()){
				pObj.getZsonList().remove((int)zp.getIndex());
			}
		}else if(zp.getKey()!=null){
			if(pObj.isMap()){
				pObj.getZsonMap().remove(zp.getKey());
			}
		}
	}*/
	
	private void deleteCollection(ZsonResultImpl zri, String path, int deletedPathIndex, String key){
		String parentKey = this.getKeyByPath(zri, this.getParentPath(path));
		int parentIndex = this.getKeyIndexInLevel(zri, parentKey);
		Object parentObject = zri.getzResultInfo().getCollections().get(parentIndex);
		ZsonObject<Object> parentObj = new ZsonObject<Object>();
		parentObj.objectConvert(parentObject);
		if(parentObj.isMap()){
			for (String k : parentObj.getZsonMap().keySet()) {
				ZsonObject<String> po = new ZsonObject<String>();
				po.objectConvert(parentObj.getZsonMap().get(k));
				if(po.isList() && po.getZsonList().get(0).equals(key)){
					parentObj.getZsonMap().remove(k);
					break;
				}else if(po.isMap() && po.getZsonMap().get(ZsonUtils.LINK).equals(key)){
					parentObj.getZsonMap().remove(k);
					break;
				}
			}
		}else if(parentObj.isList()){
			int index = 0;
			for (Object obj : parentObj.getZsonList()) {
				ZsonObject<String> po = new ZsonObject<String>();
				po.objectConvert(obj);
				if(po.isList() && po.getZsonList().get(0).equals(key)){
					parentObj.getZsonList().remove(index);
					break;
				}else if(po.isMap() && po.getZsonMap().get(ZsonUtils.LINK).equals(key)){
					parentObj.getZsonList().remove(index);
					break;
				}
				index++;
			}
		}
		zri.getzResultInfo().getCollections().remove(deletedPathIndex);
	}
	
	private ZsonPath deletePath(ZsonResultImpl zri, String path, int deletedPathIndex){
		ZsonPath zp = new ZsonPath();
		zp.setPath(path);
		ZsonObject<String> pathObj = new ZsonObject<String>();
		pathObj.objectConvert(zri.getzResultInfo().getPath().get(deletedPathIndex));
		if(pathObj.isList()){
			int index = 0;
			for (String p : pathObj.getZsonList()) {
				if(p.equals(path)){
					pathObj.getZsonList().remove(index);
					zp.setIndex(index);
					return zp;
				}
				index++;
			}
		}else if(pathObj.isMap()){
			for (String p : pathObj.getZsonMap().values()) {
				if(p.equals(path)){
					pathObj.getZsonMap().remove(p);
					zp.setKey(p);
					return zp;
				}
			}
		}
		return zp;
	}
	
	private int getKeyIndexInLevel(ZsonResultImpl zri, String key){
		int index = 0;
		for (String k : zri.getzResultInfo().getLevel()) {
			if(k.equals(key)){
				return index;
			}
			index++;
		}
		return -1;
	}
	
	private String getParentPath(String currentPath){
		String parentPath = currentPath.substring(0,currentPath.lastIndexOf('/'));
		return parentPath;
	}
	
	private String getKeyByPath(ZsonResultImpl zri, String path){
		List<Object> paths = zri.getzResultInfo().getPath();
		int index = 0;
		for (Object pathObject : paths) {
			ZsonObject<String> pathObj = new ZsonObject<String>();
			pathObj.objectConvert(pathObject);
			if(pathObj.isList()){
				for (String p : pathObj.getZsonList()) {
					if(p.equals(path)){
						return zri.getzResultInfo().getLevel().get(index);
					}
				}
			}else if(pathObj.isMap()){
				for (String p : pathObj.getZsonMap().values()) {
					if(p.equals(path)){
						return zri.getzResultInfo().getLevel().get(index);
					}
				}
			}
			index++;
		}
		return null;
	}

	@Override
	public int offset(ZsonResult zr, Object value) {
		return 0;
	}
	
	public static void main(String[] args) {
		String c = "/a";
		System.out.println(c);
	}
	
}