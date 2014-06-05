/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.state.AppState;

/**
 *
 * @author quentin
 */
public interface CamStream extends AppState {
    public byte[] getImageData();
}
