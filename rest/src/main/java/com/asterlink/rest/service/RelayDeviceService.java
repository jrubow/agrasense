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
    String deleteRelayDevice(int relay_id);
    RelayDevice getRelayDevice(int Relay_id);
    List<RelayDevice> getAllRelayDevices();
    boolean registerRelayDevice(RelayDevice device);
}
