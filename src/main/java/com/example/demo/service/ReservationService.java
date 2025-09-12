package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.model.MRoom;
import com.example.demo.model.MTime;
import com.example.demo.repository.MRoomRepository;
import com.example.demo.repository.MTimeRepository;

@Service
public class ReservationService {
    private final MTimeRepository mTimeRepository;
    private final MRoomRepository mRoomRepository;

    public ReservationService(MTimeRepository mTimeRepository, MRoomRepository mRoomRepository) {
        this.mTimeRepository = mTimeRepository;
        this.mRoomRepository = mRoomRepository;
    }

    public List<MTime> getAllTimes() {
        return mTimeRepository.findAllByOrderByTimeIdAsc();
    }

    public List<MRoom> getAllRooms() {
        return mRoomRepository.findAllByOrderByRoomIdAsc();
    }
}
