package com.audigo.audigo_back.repository.resultSet;

public interface GetBoardResultSet {
    Integer getBIdx();
    Integer getAIdx();
    String getBoardType();
    String getPublishDt();
    String getTitle();
    String getContent();
    String getTitleEn();
    String getContentEn();
    String getCdt();
    String getAdminId();
    String getAdminNm();
    String getImageUrl();
}
