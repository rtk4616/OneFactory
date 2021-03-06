package com.daoran.newfactory.onefactory.adapter.settingadapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daoran.newfactory.onefactory.R;
import com.daoran.newfactory.onefactory.activity.main.MainActivity;
import com.daoran.newfactory.onefactory.bean.loginbean.UsergetBean;
import com.daoran.newfactory.onefactory.bean.loginbean.WorkPwSwitchBean;
import com.daoran.newfactory.onefactory.util.Http.AsyncHttpResponseHandler;
import com.daoran.newfactory.onefactory.util.Http.HttpUrl;
import com.daoran.newfactory.onefactory.util.Http.NetUtil;
import com.daoran.newfactory.onefactory.util.Http.NetWork;
import com.daoran.newfactory.onefactory.util.Http.RequestParams;
import com.daoran.newfactory.onefactory.util.Http.sharedparams.SPUtils;
import com.daoran.newfactory.onefactory.util.Listener.XXListener;
import com.daoran.newfactory.onefactory.util.exception.ToastUtils;
import com.daoran.newfactory.onefactory.view.dialog.utildialog.ResponseDialog;
import com.google.gson.Gson;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * 切换账号适配
 * Created by lizhipeng on 2017/7/7.
 */

public class WorkPwSwitchAdapter extends BaseAdapter {
    private Context context;//上下文
    private Activity mActivity;
    private List<WorkPwSwitchBean.Data> switchBeendatalist
            = new ArrayList<WorkPwSwitchBean.Data>();//保存账号信息的实体集合
    private SPUtils spUtils;
    private SharedPreferences sp;
    private XXListener mXXListener;//回调接口
    private FragmentManager fManager;
    private FragmentTransaction ftransaction;

    public void setOnXXClickListener(XXListener XXListener) {
        this.mXXListener = XXListener;
    }

    public WorkPwSwitchAdapter(Context context, List<WorkPwSwitchBean.Data> switchBeendatalist) {
        this.context = context;
        this.switchBeendatalist = switchBeendatalist;
    }

    @Override
    public int getCount() {
        return switchBeendatalist.size();
    }

    @Override
    public WorkPwSwitchBean.Data getItem(int position) {
        return switchBeendatalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_work_pwswitch, null);
            viewHolder.rl_item_pwswitch = (RelativeLayout) convertView.findViewById(R.id.rl_item_pwswitch);
            viewHolder.tvWorkPwswitch = (TextView) convertView.findViewById(R.id.tvWorkPwswitch);
            viewHolder.image_check_pwswitch = (ImageView) convertView.findViewById(R.id.image_check_pwswitch);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        sp = context.getSharedPreferences("my_sp", 0);
        viewHolder.tvWorkPwswitch.setText(getItem(position).getU_name());
        String username = sp.getString("name", "");//当前登录人
        if (username.equals(getItem(position).getU_name())) {//判断当前登录人并且将标志图片显示
            viewHolder.image_check_pwswitch.setVisibility(View.VISIBLE);
        } else {
            viewHolder.image_check_pwswitch.setVisibility(View.GONE);
        }
        //点击某个用户后，将这个用户的账号和密码传到登录方法，然后登录
        viewHolder.rl_item_pwswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uname = getItem(position).getLogid();//获得账号
                String upassword = getItem(position).getPasswork();//获得密码
                postLogin(uname, upassword);//传递账号和密码
            }
        });
        return convertView;
    }


    /**
     * 切换登录
     * 此方法和登录页方法基本一致
     *
     * @param uname
     * @param upassword
     */
    private void postLogin(final String uname, final String upassword) {
        String loginuserUrl = HttpUrl.debugoneUrl + "Login/UserLogin/";
        if (NetWork.isNetWorkAvailable(context)) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(NetUtil.createParam("Logid", uname));
            params.add(NetUtil.createParam("pwd", upassword));
            params.add(NetUtil.createParam("Ischeckpwd", true));
            params.add(NetUtil.createParam("Company", "杭州道然进出口有限公司"));
            RequestParams requestParams = new RequestParams(params);
            NetUtil.getAsyncHttpClient().post(loginuserUrl, requestParams, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String content) {
                    super.onSuccess(content);
                    System.out.print(content);//打印返回的数据
                    Gson gson = new Gson();
                    UsergetBean userBean = gson.fromJson(content, UsergetBean.class);
                    //判断用户名和密码是否正确
                    if (userBean.isStatus() == true) {
                        MainActivity.maininstance.finish();
                        spUtils.put(context, "username", uname);
                        spUtils.put(context, "passwd", upassword);
                        spUtils.put(context.getApplicationContext(),
                                "name", userBean.getU_name());//用户名,适用于总体项目
                        spUtils.put(context.getApplicationContext(),
                                "FTYDLName", userBean.getU_name());//用户名，适用于生产日报模块
                        spUtils.put(context.getApplicationContext(),
                                "QACworkDialogPrddocumentary", userBean.getU_name());//用户名，适用于查货跟踪模块
                        spUtils.put(context.getApplicationContext(),
                                "commologinid", userBean.getLogid());//账号
                        Intent intent = new Intent(context, MainActivity.class);
                        Bundle bundle = new Bundle();//实例化传递参数的类（bundle是一个key-value对，传递时需要一一对应key值和value值）
                        spUtils.put(context, "u_name_value", "1");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//设置由不是activity的页面跳转别的activity
                        intent.putExtras(bundle);//将获取到的用户名传递到将要跳转的页面
                        context.startActivity(intent);
                        ResponseDialog.closeLoading();
                    } else {
                        ToastUtils.ShowToastMessage("用户名密码错误，请重新输入", context);
                        ResponseDialog.closeLoading();
                    }
                }

                @Override
                public void onFailure(Throwable error, String content) {
                    super.onFailure(error, content);
                    ToastUtils.ShowToastMessage("登录失败", context);
                    ResponseDialog.closeLoading();
                }

                @Override
                public void onFinish() {
                    super.onFinish();
//                    mXXListener.onXXClick();//方法执行完后，回调接口
                    ResponseDialog.closeLoading();
                }
            });
        } else {
            ToastUtils.ShowToastMessage(context.getString(R.string.noHttp), context);
        }
    }

    static class ViewHolder {
        RelativeLayout rl_item_pwswitch;
        TextView tvWorkPwswitch;//显示的用户名控件
        ImageView image_check_pwswitch;//当前的登录状态
    }
}