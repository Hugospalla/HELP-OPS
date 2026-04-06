package commons.interfaces;

import java.rmi.RemoteException;

public interface ISupervisionInternal extends java.rmi.Remote {

	public void notifierEvenement(String message) throws RemoteException;
}
