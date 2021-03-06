package com.haikuowuya.microlife.util;

import android.content.Context;
import android.text.TextUtils;

import com.haikuowuya.microlife.Constants;
import com.haikuowuya.microlife.base.BaseActivity;
import com.haikuowuya.microlife.mvp.model.CityItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by raiyi-suzhou on 2015/5/14 0014.
 */
public class CityUtils
{
    private static final String BO_ZHOU_CITY="亳州" ;
    private static final int BO_ZHOU_ID=11260;
    private static final String TAG_CITY_SORT = "citySort";
    private static final String TAG_CITY_LIST = "cityList";
    private static final String TAG_KEY = "key";
    private static final String TAG_S_NAME = "sName";
    private static final String TAG_NAME = "name";
    private static final String TAG_ALL_LETORDER = "allletorder";
    private static final String TAG_ALL_ORDER = "allorder";
    private static final String TAG_AREA_CODE = "areaCode";
    private static final String TAG_CDMA_ID = "cdmaId";
    private static final String TAG_DD_Id = "ddId";
    private static final String TAG_ID = "id";
    private static final String TAG_LEVEL = "level";
    private static final String TAG_LNG_LAT = "lngLat";
    private static final String TAG_WEATHER_ID = "weatherId";
    private static final String TAG_WL_ID = "wlId";
    private static final String DEFAULT_LNG_LAT="116.395645,39.929986";      //默认北京
    private static final int DEFAULT_ID = 10002;//默认北京
    private static final int DEFAULT_CDMA_ID=13824;//默认北京

    public static LinkedList<CityItem> getCityList(Context context)
    {
        LinkedList<CityItem> items = null;
        String cityJson = AssetUtils.getCityJson(context);
        if (!TextUtils.isEmpty(cityJson))
        {
            items = new LinkedList<CityItem>();
            try
            {
                JSONObject jsonObject = new JSONObject(cityJson);
                JSONArray jsonArray = jsonObject.optJSONArray(TAG_CITY_SORT);
                if (null != jsonArray)
                {
                    String key = "";
                    String sName, name, areaCode, ddId, lngLat, weatherId, wlId;
                    int allletorder, allorder, cdmaId, id, level;
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        jsonObject = jsonArray.getJSONObject(i);
                        key = jsonObject.optString(TAG_KEY);
                        JSONArray cityJsonArray = jsonObject.optJSONArray(TAG_CITY_LIST);
                        if (null != cityJsonArray)
                        {
                            for (int k = 0; k < cityJsonArray.length(); k++)
                            {
                                JSONObject cityJsonObject = cityJsonArray.optJSONObject(k);
                                if (cityJsonObject != null)
                                {
                                    sName = cityJsonObject.optString(TAG_S_NAME);
                                    name = cityJsonObject.optString(TAG_NAME);
                                    allletorder = cityJsonObject.optInt(TAG_ALL_LETORDER, 1);
                                    allorder = cityJsonObject.optInt(TAG_ALL_ORDER, 1);
                                    areaCode = cityJsonObject.optString(TAG_AREA_CODE);
                                    cdmaId = cityJsonObject.optInt(TAG_CDMA_ID, DEFAULT_CDMA_ID);//北京
                                    ddId = cityJsonObject.optString(TAG_DD_Id);
                                    id = cityJsonObject.optInt(TAG_ID, DEFAULT_ID);//北京
                                    level = cityJsonObject.optInt(TAG_LEVEL, 0);
                                    lngLat = cityJsonObject.optString(TAG_LNG_LAT, DEFAULT_LNG_LAT);
                                    weatherId = cityJsonObject.optString(TAG_WEATHER_ID);
                                    wlId = cityJsonObject.optString(TAG_WL_ID);
                                    if(BO_ZHOU_ID == id)
                                    {
                                        sName = BO_ZHOU_CITY;
                                    }
                                    CityItem cityItem = new CityItem();
                                    cityItem.setKey(key);
                                    cityItem.setsName(sName);
                                    cityItem.setName(name);
                                    cityItem.setAllletorder(allletorder);
                                    cityItem.setAreaCode(areaCode);
                                    cityItem.setAllorder(allorder);
                                    cityItem.setCdmaId(cdmaId);
                                    cityItem.setDdId(ddId);
                                    cityItem.setId(id);
                                    cityItem.setLevel(level);
                                    cityItem.setLngLat(lngLat);
                                    cityItem.setWeatherId(weatherId);
                                    cityItem.setWlId(wlId);
                                    cityItem.setId_id(items.size());
                                    cityItem.setIsCurrentCity(Constants.DEFAULT_CITY.equals(sName));
                                    String city = "城市名称 = " + cityItem.getsName() + " 关键词 = " + cityItem.getKey()
                                            +" 城市地区代码 = "+areaCode +" 城市天气代码 = "+weatherId;
                                  //  System.out.println("city = " + city);
                                    items.add(cityItem);
                                }
                            }
                        }
                    }
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            items.addFirst(genLocationCity());
            System.out.println("总共城市列表个数 = " + items.size());
            if (items.size() ==1)
            {
                items = null;
            }
        }
        return items;
    }
    private static CityItem genLocationCity()
    {
        String locationCity = "正在定位……";
        CityItem cityItem = new CityItem();
        cityItem.setKey("定位城市");
        cityItem.setId_id(-1);
        cityItem.setsName(locationCity);
        return cityItem;
    }

     public static CityItem genDefaultCity()
     {
         CityItem cityItem = new CityItem();
         return  cityItem;

     }
    private static  void clearCurrentCityFlag(BaseActivity activity)
    {
        Realm realm =  Realm.getInstance(activity) ;
        RealmQuery<CityItem> query =realm.where(CityItem.class).equalTo(CityItem.FIELD_IS_CURRENT_CITY, true);
        if(query != null)
        {
            RealmResults<CityItem> result = query.findAll();
            for(int i = 0;i < result.size();i++)
            {
                realm.beginTransaction();;
                CityItem cityItem = result.get(i);
                cityItem.setIsCurrentCity(false);
                realm.commitTransaction();;
            }

        }

    }

    public  static  CityItem  saveSelectedCity(BaseActivity activity ,String city)
    {
        clearCurrentCityFlag(activity);
        CityItem  selectedCityItem = null;
        Realm realm =  Realm.getInstance(activity) ;
        RealmQuery<CityItem> query =realm.where(CityItem.class).equalTo(CityItem.FIELD_S_NAME, city);
        if (null != query)
        {
            RealmResults<CityItem> result = query.findAll();
            CityItem cityItem = result.get(0);
            selectedCityItem = cityItem;
            System.out.println("选择的城市  = " + cityItem.getsName());
            activity.getPreferences().edit().remove(Constants.PREF_WEATHER_JSON).commit();
//                ACache aCache = ACache.get(mActivity);
//                aCache.put(Constants.PREF_SELECTED_CITY, cityItem);
            for(int i = 0;i < result.size();i++)
            {
                realm.beginTransaction();;
                CityItem item = result.get(i);
                item.setIsCurrentCity(true);
                realm.commitTransaction();;
            }
        }
        return  selectedCityItem;
    }

}
