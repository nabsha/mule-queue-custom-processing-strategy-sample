package org.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mule.api.store.ListableObjectStore;
import org.mule.api.store.ObjectStoreException;
import org.mule.api.store.QueueStore;
import org.mule.config.i18n.CoreMessages;
import org.mule.util.store.AbstractObjectStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("deprecation")
public class SimpleMemorySortedObjectStore<T extends Serializable> extends AbstractObjectStore<T>
implements ListableObjectStore<T>
{
	private static final Logger logger = LoggerFactory.getLogger(SimpleMemorySortedObjectStore.class);

	private Map<Serializable, T> map = Collections.synchronizedMap(new HashMap<Serializable, T>());
	
	@Override
	public boolean isPersistent()
	{		
		logger.debug("isPersistent: true");
	    return true;
	}
	
	@Override
	protected boolean doContains(Serializable key)
	{
		logger.debug("doContains# key:" + key.toString());
	    return map.containsKey(key);
	}
	
	@Override
	protected void doStore(Serializable key, T value) throws ObjectStoreException
	{
		logger.debug("doStore# key:" + key.toString() + ", value:" + value.toString());
	    if (value == null)
	    {
	        throw new ObjectStoreException(CoreMessages.objectIsNull("value"));
	    }
	
	    map.put(key, value);
	}
	
	@Override
	protected T doRetrieve(Serializable key)
	{
		logger.debug("doRetrieve# key:" + key.toString() );

	    return (T) "Dummy";
	}
	
	@Override
	protected T doRemove(Serializable key)
	{
		logger.debug("doRemove# key:" + key.toString() );
		return (T) "Dummy";
		//return map.remove(key);
	}
	
	@Override
	public void open() throws ObjectStoreException
	{
	    // this is a no-op
	}
	
	@Override
	public void close() throws ObjectStoreException
	{
	    // this is a no-op
	}
	
	@Override
	public List<Serializable> allKeys() throws ObjectStoreException
	{
		logger.debug("allKeys# map.keySet():" + map.keySet().size() );

	    return new ArrayList<Serializable>(map.keySet());
	}

	@Override
	public void clear() throws ObjectStoreException {
		// TODO Auto-generated method stub
		
	}
}