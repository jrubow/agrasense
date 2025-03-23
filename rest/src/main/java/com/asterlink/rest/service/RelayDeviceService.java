package com.asterlink.rest.service;
import com.asterlink.rest.model.RelayDevice;
import java.util.List;
import java.util.Map;

/**
 * Interface for Relay Device services
 * @author jrubow
 */

public interface RelayDeviceService {
    boolean createRelayDevice(RelayDevice device);
    boolean createRelayDeviceBatch(List<RelayDevice> devices);
    String updateRelayDevice(Map<String, Object> updates);
    String deleteRelayDevice(long relay_id);
    RelayDevice getRelayDevice(long Relay_id);
    List<RelayDevice> getAllRelayDevices();
    List<RelayDevice> getRelayDevicesBySentinelId(long sentinelId);
    boolean registerRelayDevice(RelayDevice device);
}
