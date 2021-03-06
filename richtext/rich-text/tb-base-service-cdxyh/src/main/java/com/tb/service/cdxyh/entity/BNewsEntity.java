package com.tb.service.cdxyh.entity;

import com.sticker.online.core.entity.BaseEntity;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@DataObject(generateConverter = true)
@Table(name = "b_news")
public class BNewsEntity extends BaseEntity {
    public BNewsEntity() {
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json = super.toJson();
        BNewsEntityConverter.toJson(this, json);
        return json;
    }

    public BNewsEntity(JsonObject jsonObject) {
        super(jsonObject);
        BNewsEntityConverter.fromJson(jsonObject, this);
    }


    /**
     * id
     */
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @GenericGenerator(name = "jpa-uuid", strategy = "uuid")
    @Column(name = "id",length = 32)
    private String id;


    /**
     * 新闻标题
     */
    @Column(name = "title")
    private String title;
    /**
     * 新闻内容
     */
    @Column(name = "contents")
    private String contents;
    /**
     * 新闻缩略图
     */
    @Column(name = "thumb")
    private String thumb;
    /**
     * 新闻描述
     */
    @Column(name = "description")
    private String description;
    /**
     * 浏览量
     */
    @Column(name = "view_count")
    private Integer viewCount;
    /**
     * 是否顶置
     */
    @Column(name = "istop")
    private Integer istop;
    /**
     * 资讯类型 0:组织资讯;1:校庆资讯;2:校友会资讯
     */
    @Column(name = "type")
    private Integer type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getIstop() {
        return istop;
    }

    public void setIstop(Integer istop) {
        this.istop = istop;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
