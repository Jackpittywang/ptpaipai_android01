package com.sunnybear.library.controller.task;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v4.app.FragmentActivity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 超级任务(替代AsyncTask)
 * Created by guchenkai on 2015/12/8.
 */
public class SuperTask {
    /**
     * 后台线程接口,
     * 通过实现这个接口,添加你想要的功能.
     */
    public interface TaskDescription<T> {
        T onBackground();
    }

    /**
     * 主线程接口,
     * 用于接收处理来自后台线程的消息,可用于更新UI.
     */
    public interface MessageListener {
        void handleMessage(@NonNull Message message);
    }

    /**
     * 主线程接口,
     * 当后台线程正常结束运行,且当前上下文环境处于安全的生命周期中,
     * 这个接口中的方法将会被调用.
     */
    public interface FinishListener<T> {
        void onFinish(@Nullable T result);
    }

    /**
     * 主线程接口,
     * 当后台线程因为异常结束运行,且当前上下文环境处于安全的生命周期中,
     * 这个接口中的方法将会被调用.
     */
    public interface BrokenListener {
        void onBroken(@NonNull Exception e);
    }

    public class Register {
        private Integer id;

        private Register(@NonNull Integer id) {
            this.id = id;
        }

        /**
         * 必须
         */
        @MainThread
        public Builder assign(@NonNull TaskDescription description) {
            taskMap.put(id, description);
            return new Builder(id);
        }
    }

    public class Builder {
        private Integer id;

        private Builder(@NonNull Integer id) {
            this.id = id;
        }

        /**
         * 可选
         */
        @MainThread
        public Builder handle(@NonNull MessageListener listener) {
            messageMap.put(id, listener);
            return this;
        }

        /**
         * 可选
         */
        @MainThread
        public Builder finish(@NonNull FinishListener listener) {
            finishMap.put(id, listener);
            return this;
        }

        /**
         * 可选
         */
        @MainThread
        public Builder broken(@NonNull BrokenListener listener) {
            brokenMap.put(id, listener);
            return this;
        }

        /**
         * 必须
         */
        @MainThread
        public void execute() {
            executor.execute(buildRunnable(id));
        }
    }

    private class Holder {
        private Integer id;
        private Object object;

        private Holder(@NonNull Integer id, @Nullable Object object) {
            this.id = id;
            this.object = object;
        }
    }

    /**
     * 当你在后台线程中发送消息的时候,
     * 你的message.what不应该和MESSAGE_*相等.
     */
    public static final int MESSAGE_FINISH = 0x65530;

    public static final int MESSAGE_BROKEN = 0x65531;

    public static final int MESSAGE_STOP = 0x65532;

    public static final String TAG_HOOK = "HOOK";

    private static final Integer ID_ACTIVITY = 0x65533;

    private static final Integer ID_FRAGMENT_ACTIVITY = 0x65534;

    private static final Integer ID_FRAGMENT = 0x65535;

    private static final Integer ID_SUPPORT_FRAGMENT = 0x65536;

    /**
     * 那么怎么样才能实时获得当前上下文环境的状态呢?
     * 很简单,只需要通过Activity/FragmentActivity(v4)/Fragment/Fragment(v4)的FragmentManager加入一个hook fragment,
     * 这个hook fragment的生命周期会跟随它所依附的上下文环境,
     * 所以我们就可以通过它拿到当前上下文环境的生命周期啦.
     */
    public static class HookFragment extends Fragment {
        protected boolean postEnable = true;

        @Override
        public void onStop() {
            super.onStop();

            if (postEnable) {
                Message message = new Message();
                message.what = MESSAGE_STOP;
                post(message);
            }
        }
    }

    public static class HookSupportFragment extends android.support.v4.app.Fragment {
        protected boolean postEnable = true;

        @Override
        public void onStop() {
            super.onStop();

            if (postEnable) {
                Message message = new Message();
                message.what = MESSAGE_STOP;
                post(message);
            }
        }
    }

    /**
     * 链式调用从这里开始,形式看上去差不是这样的:
     * SugarTask.with().assign().handle().finish().broken().execute();
     */
    @MainThread
    public static Register with(@NonNull Activity activity) {
        getInstance().registerHookToContext(activity);
        return getInstance().buildRegister(activity);
    }

    @MainThread
    public static Register with(@NonNull FragmentActivity activity) {
        getInstance().registerHookToContext(activity);
        return getInstance().buildRegister(activity);
    }

    @MainThread
    public static Register with(@NonNull Fragment fragment) {
        getInstance().registerHookToContext(fragment);
        return getInstance().buildRegister(fragment);
    }

    @MainThread
    public static Register with(@NonNull android.support.v4.app.Fragment fragment) {
        getInstance().registerHookToContext(fragment);
        return getInstance().buildRegister(fragment);
    }

    /**
     * 使用这个方法,在后台线程中向主线程发送消息:
     * SugarTask.post(YOUR MESSAGE);
     */
    @WorkerThread
    public static void post(@NonNull Message message) {
        getInstance().handler.sendMessage(message);
    }

    /**
     * 获得Handler实例
     *
     * @return Handler实例
     */
    public static Handler handler() {
        return getInstance().handler;
    }

    private static class SugarTaskHolder {
        public static final SuperTask INSTANCE = new SuperTask();
    }

    private static SuperTask getInstance() {
        return SugarTaskHolder.INSTANCE;
    }

    /**
     * 每个执行的线程都有一个唯一的id,通过这个id我们可以很方便地管理线程.
     */
    private static AtomicInteger count = new AtomicInteger(0);

    /**
     * 用于承载当前上下文环境(Activity/FragmentActivity(v4)/Fragment/Fragment(v4),
     * 当当前上下文环境的生命周期停止时,记住使用 resetHolder()将holder重置.
     */
    private Holder holder = null;

    private Map<Integer, TaskDescription> taskMap = new ConcurrentHashMap<>();

    private Map<Integer, MessageListener> messageMap = new ConcurrentHashMap<>();

    private Map<Integer, FinishListener> finishMap = new ConcurrentHashMap<>();

    private Map<Integer, BrokenListener> brokenMap = new ConcurrentHashMap<>();

    private Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 8);

    /**
     * 当message.what = MESSAGE_STOP,我们立即清除所有在主线程中的回调,
     * 这样就做到了后台线程和主线程的解耦,有效避免OOM.
     */
    private Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (message.what == MESSAGE_FINISH && message.obj instanceof Holder) {
                Holder result = (Holder) message.obj;

                taskMap.remove(result.id);
                messageMap.remove(result.id);
                brokenMap.remove(result.id);

                FinishListener listener = finishMap.remove(result.id);
                if (listener != null)
                    listener.onFinish(result.object);
                getInstance().dispatchUnregister();
            } else if (message.what == MESSAGE_BROKEN && message.obj instanceof Holder) {
                Holder result = (Holder) message.obj;

                taskMap.remove(result.id);
                messageMap.remove(result.id);
                finishMap.remove(result.id);

                BrokenListener listener = brokenMap.remove(result.id);
                if (listener != null)
                    listener.onBroken((Exception) result.object);
                getInstance().dispatchUnregister();
            } else if (message.what == MESSAGE_STOP) {
                resetHolder();

                taskMap.clear();
                messageMap.clear();
                finishMap.clear();
                brokenMap.clear();
            } else {
                for (MessageListener listener : messageMap.values()) {
                    listener.handleMessage(message);
                }
            }
            return true;
        }
    });

    private Register buildRegister(@NonNull Activity activity) {
        holder = new Holder(ID_ACTIVITY, activity);
        return new Register(count.getAndIncrement());
    }

    private Register buildRegister(@NonNull FragmentActivity activity) {
        holder = new Holder(ID_FRAGMENT_ACTIVITY, activity);
        return new Register(count.getAndIncrement());
    }

    private Register buildRegister(@NonNull Fragment fragment) {
        holder = new Holder(ID_FRAGMENT, fragment);
        return new Register(count.getAndIncrement());
    }

    private Register buildRegister(@NonNull android.support.v4.app.Fragment fragment) {
        holder = new Holder(ID_SUPPORT_FRAGMENT, fragment);
        return new Register(count.getAndIncrement());
    }

    private Runnable buildRunnable(@NonNull final Integer id) {
        return new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                /**
                 * 线程安全问题.
                 */
                if (taskMap.containsKey(id)) {
                    Message message = Message.obtain();
                    try {
                        message.what = MESSAGE_FINISH;
                        message.obj = new Holder(id, taskMap.get(id).onBackground());
                    } catch (Exception e) {
                        message.what = MESSAGE_BROKEN;
                        message.obj = new Holder(id, e);
                    }
                    post(message);
                }
            }
        };
    }

    private void registerHookToContext(@NonNull Activity activity) {
        FragmentManager manager = activity.getFragmentManager();
        HookFragment hookFragment = (HookFragment) manager.findFragmentByTag(TAG_HOOK);
        if (hookFragment == null) {
            hookFragment = new HookFragment();
            manager.beginTransaction().add(hookFragment, TAG_HOOK).commitAllowingStateLoss();
        }
    }

    private void registerHookToContext(@NonNull FragmentActivity activity) {
        android.support.v4.app.FragmentManager manager = activity.getSupportFragmentManager();
        HookSupportFragment hookSupportFragment = (HookSupportFragment) manager.findFragmentByTag(TAG_HOOK);
        if (hookSupportFragment == null) {
            hookSupportFragment = new HookSupportFragment();
            manager.beginTransaction().add(hookSupportFragment, TAG_HOOK).commitAllowingStateLoss();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void registerHookToContext(@NonNull Fragment fragment) {
        FragmentManager manager = fragment.getChildFragmentManager();
        HookFragment hookFragment = (HookFragment) manager.findFragmentByTag(TAG_HOOK);
        if (hookFragment == null) {
            hookFragment = new HookFragment();
            manager.beginTransaction().add(hookFragment, TAG_HOOK).commitAllowingStateLoss();
        }
    }

    private void registerHookToContext(@NonNull android.support.v4.app.Fragment fragment) {
        android.support.v4.app.FragmentManager manager = fragment.getChildFragmentManager();
        HookSupportFragment hookSupportFragment = (HookSupportFragment) manager.findFragmentByTag(TAG_HOOK);
        if (hookSupportFragment == null) {
            hookSupportFragment = new HookSupportFragment();
            manager.beginTransaction().add(hookSupportFragment, TAG_HOOK).commitAllowingStateLoss();
        }
    }

    private void dispatchUnregister() {
        if (holder == null || taskMap.size() > 0) return;
        if (holder.id.equals(ID_ACTIVITY) && holder.object instanceof Activity)
            unregisterHookToContext((Activity) holder.object);
        else if (holder.id.equals(ID_FRAGMENT_ACTIVITY) && holder.object instanceof FragmentActivity)
            unregisterHookToContext((FragmentActivity) holder.object);
        else if (holder.id.equals(ID_FRAGMENT) && holder.object instanceof Fragment)
            unregisterHookToContext((Fragment) holder.object);
        else if (holder.id.equals(ID_SUPPORT_FRAGMENT) && holder.object instanceof android.support.v4.app.Fragment)
            unregisterHookToContext((android.support.v4.app.Fragment) holder.object);
        resetHolder();
    }

    private void unregisterHookToContext(@NonNull Activity activity) {
        FragmentManager manager = activity.getFragmentManager();
        HookFragment hookFragment = (HookFragment) manager.findFragmentByTag(TAG_HOOK);
        if (hookFragment != null) {
            hookFragment.postEnable = false;
            manager.beginTransaction().remove(hookFragment).commitAllowingStateLoss();
        }
    }

    private void unregisterHookToContext(@NonNull FragmentActivity activity) {
        android.support.v4.app.FragmentManager manager = activity.getSupportFragmentManager();
        HookSupportFragment hookSupportFragment = (HookSupportFragment) manager.findFragmentByTag(TAG_HOOK);
        if (hookSupportFragment != null) {
            hookSupportFragment.postEnable = false;
            manager.beginTransaction().remove(hookSupportFragment).commitAllowingStateLoss();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void unregisterHookToContext(@NonNull Fragment fragment) {
        FragmentManager manager = fragment.getChildFragmentManager();
        HookFragment hookFragment = (HookFragment) manager.findFragmentByTag(TAG_HOOK);
        if (hookFragment != null) {
            hookFragment.postEnable = false;
            manager.beginTransaction().remove(hookFragment).commitAllowingStateLoss();
        }
    }

    private void unregisterHookToContext(@NonNull android.support.v4.app.Fragment fragment) {
        android.support.v4.app.FragmentManager manager = fragment.getChildFragmentManager();
        HookSupportFragment hookSupportFragment = (HookSupportFragment) manager.findFragmentByTag(TAG_HOOK);
        if (hookSupportFragment != null) {
            hookSupportFragment.postEnable = false;
            manager.beginTransaction().remove(hookSupportFragment).commitAllowingStateLoss();
        }
    }

    private void resetHolder() {
        if (holder == null) return;
        holder.id = 0;
        holder.object = null;
        holder = null;
    }
}