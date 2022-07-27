package com.example.handlemanager.repository;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.handlemanager.model.HandleIdx;
import com.example.handlemanager.model.Handles;

@Repository
public interface HandleRepository extends JpaRepository<Handles, HandleIdx> {
	// Get all handles	
	@Query(value="select distinct handle from handles h", nativeQuery=true)
	List<byte[]> getHandles();	
	
	// Resolve single handle
	@Query(value="select * from handles where handle = ?1", nativeQuery=true)
	List<Handles> resolveHandle(byte[] handle);
	
	// Given list of handles, which ones are already taken?
	@Query(value = "select distinct handle from handles where handle in :hdls", nativeQuery=true)
	List<byte[]> checkDuplicateHandles(List<byte[]> hdls);
	
	// Delete handle record
	@Modifying
	@Transactional
	@Query(value= "delete from handles where handle = ?1", nativeQuery=true)
	void deleteHandleRecord(byte[] handle);
	
	// Given handle and index to modify, update handle record with supplied data
	@Modifying
	@Transactional
	@Query(value="update handles h set data=:data where h.handle=:hdl and h.idx=:i",nativeQuery=true)
	void updateHandleRecordData(byte[] data, byte[] hdl, int i);
	
}


