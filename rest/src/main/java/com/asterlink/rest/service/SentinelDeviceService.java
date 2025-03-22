package com.asterlink.rest.service;
import com.asterlink.rest.model.SentinelDevice;
import java.util.List;
import java.util.Map;

/**
 * Interface for Sentinel Device services
 * @author jrubow
 */

public interface SentinelDeviceService {
    public int createSentinelDevice(SentinelDevice device);
    public boolean updateSentinelDevice(Map<String, Object> updates);
    public String deleteSentinelDevice(int id);
    public SentinelDevice getSentinelDevice(int id);
    public List<SentinelDevice> getAllSentinelDevices();
    public String claimSentinelDevice(int deviceId, String password, int clientId);
    public List<SentinelDevice> findByClientId(int clientId);
}
