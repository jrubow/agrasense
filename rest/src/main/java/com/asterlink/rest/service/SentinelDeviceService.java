package com.asterlink.rest.service;
import com.asterlink.rest.model.SentinelDevice;
import java.util.List;
import java.util.Map;

/**
 * Interface for Sentinel Device services
 * @author jrubow
 */

public interface SentinelDeviceService {
    public long createSentinelDevice(SentinelDevice device);
    public long createSentinelDeviceBatch(List<SentinelDevice> devices);
    public boolean updateSentinelDevice(Map<String, Object> updates);
    public String deleteSentinelDevice(long id);
    public SentinelDevice getSentinelDevice(long id);
    public List<SentinelDevice> getAllSentinelDevices();
    public String claimSentinelDevice(long deviceId, String password, int clientId);
    public List<SentinelDevice> findByClientId(int clientId);
}
