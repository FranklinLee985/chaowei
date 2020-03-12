package com.classroomsdk.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class NotificationCenter {

    final private HashMap<Integer, ArrayList<Object>> observers = new HashMap<Integer, ArrayList<Object>>();

    final private HashMap<String, Object> memCache = new HashMap<String, Object>();

    final private HashMap<Integer, Object> removeAfterBroadcast = new HashMap<Integer, Object>();
    final private HashMap<Integer, Object> addAfterBroadcast = new HashMap<Integer, Object>();
    final private ArrayList<Object> removeAfterBroadcastClass = new ArrayList<Object>();

    private boolean broadcasting = false;

    private static volatile NotificationCenter Instance = null;

    public static NotificationCenter getInstance() {
        NotificationCenter localInstance = Instance;
        if (localInstance == null) {
            synchronized (NotificationCenter.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new NotificationCenter();
                }
            }
        }
        return localInstance;
    }

    public void resetInstance() {
        Instance = null;
    }

    public interface NotificationCenterDelegate {  // ������Ϣ�߱���ʵ�ִ˽ӿ�
        public abstract void didReceivedNotification(int id, Object... args);
    }

    public void addToMemCache(int id, Object object) {//��ӵ�����
        addToMemCache(String.valueOf(id), object);
    }

    public void addToMemCache(String id, Object object) {
        memCache.put(id, object);
    }

    public Object getFromMemCache(int id) {
        return getFromMemCache(String.valueOf(id), null);
    }

    public Object getFromMemCache(String id, Object defaultValue) {
        Object obj = memCache.get(id);
        if (obj != null) {
            memCache.remove(id);
            return obj;
        }
        return defaultValue;
    }

    /**
     * ����Ϣ
     *
     * @param id
     * @param args
     */
    public void postNotificationName(int id, Object... args) {
        synchronized (observers) {
            broadcasting = true;
            ArrayList<Object> objects = observers.get(id);
            if (objects != null) {
                for (Object obj : objects) {
                     ((NotificationCenterDelegate) obj).didReceivedNotification(id, args);
                }
            }
            broadcasting = false;
            if (!removeAfterBroadcast.isEmpty()) {
                for (HashMap.Entry<Integer, Object> entry : removeAfterBroadcast.entrySet()) {
                    removeObserver(entry.getValue(), entry.getKey());
                }
                removeAfterBroadcast.clear();
            }
            if (!addAfterBroadcast.isEmpty()) {
                for (HashMap.Entry<Integer, Object> entry : addAfterBroadcast.entrySet()) {
                    addObserver(entry.getValue(), entry.getKey());
                }
                addAfterBroadcast.clear();
            }
            if (!removeAfterBroadcastClass.isEmpty()) {
                for (int i = 0; i < removeAfterBroadcastClass.size(); i++) {
                    removeObserver(removeAfterBroadcastClass.get(i));
                }
                removeAfterBroadcastClass.clear();
            }
        }
    }

    public void postNotificationName2(int id, Object... args) {
        synchronized (observers) {
            broadcasting = true;
            ArrayList<Object> objects = observers.get(id);
            if (objects != null) {
                for (Object obj : objects) {
                    ((NotificationCenterDelegate) obj).didReceivedNotification(id, args);
                }
            }
            broadcasting = false;
            if (!removeAfterBroadcast.isEmpty()) {
                for (HashMap.Entry<Integer, Object> entry : removeAfterBroadcast.entrySet()) {
                    removeObserver(entry.getValue(), entry.getKey());
                }
                removeAfterBroadcast.clear();
            }
            if (!addAfterBroadcast.isEmpty()) {
                for (HashMap.Entry<Integer, Object> entry : addAfterBroadcast.entrySet()) {
                    addObserver(entry.getValue(), entry.getKey());
                }
                addAfterBroadcast.clear();
            }
        }
    }

    /**
     * ��עĳ����Ϣ
     *
     * @param observer ��Ϣ�����ߣ�����ʵ��NotificationCenterDelegate �ӿ�
     * @param id       ��Ϣid����ʾҪ��ע����Ϣ����
     */
    public void addObserver(Object observer, int id) {
        synchronized (observers) {
            if (broadcasting) {
                addAfterBroadcast.put(id, observer);
                return;
            }

            if (removeAfterBroadcastClass.contains(observer))
                removeAfterBroadcastClass.remove(observer);

            ArrayList<Object> objects = observers.get(id);
            if (objects == null) {
                observers.put(id, (objects = new ArrayList<Object>()));
            }
            if (objects.contains(observer)) {
                return;
            }
            //if( id == MessagesController.contactsDidLoaded)
            //FileLog.d("emm", "postNotificationName addObserver");
            objects.add(observer);
        }
    }

    public void removeObserver(Object observer) {
        synchronized (observers) {
            if (broadcasting) {
                removeAfterBroadcastClass.add(observer);
                return;
            }
            Iterator<HashMap.Entry<Integer, ArrayList<Object>>> iter = observers.entrySet().iterator();
            while (iter.hasNext()) {
                HashMap.Entry entry = (HashMap.Entry) iter.next();
                ArrayList<Object> objectsl = (ArrayList<Object>) entry.getValue();
                objectsl.remove(observer);
            }
        }
    }

    /**
     * ֹͣ��עĳ����Ϣ
     *
     * @param observer��Ϣ������
     * @param id                ��Ϣid��Ҫֹͣ��ע����Ϣ����
     */
    public void removeObserver(Object observer, int id) {
        synchronized (observers) {
            if (broadcasting) {
                removeAfterBroadcast.put(id, observer);
                return;
            }
            ArrayList<Object> objects = observers.get(id);
            if (objects != null) {
                objects.remove(observer);
                if (objects.size() == 0) {
                    observers.remove(id);
                }
            }
        }
    }

    /**
     * �Ƴ�������Ϣ��ע�ߣ�ͨ�����˳�����ʱ����
     */
    public void removeAllObservers() {
        synchronized (observers) {
            observers.clear();
        }
    }
}