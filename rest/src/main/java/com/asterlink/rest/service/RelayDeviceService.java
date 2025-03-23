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
    String deleteRelayDevice(long relayId);
    RelayDevice getRelayDevice(long relayId);
    List<RelayDevice> getAllRelayDevices();
    List<RelayDevice> getRelayDevicesBySentinelId(long sentinelId);
    boolean registerRelayDevice(RelayDevice device);
}
