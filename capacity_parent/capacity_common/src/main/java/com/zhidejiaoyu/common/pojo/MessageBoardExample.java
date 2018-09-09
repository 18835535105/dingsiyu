package com.zhidejiaoyu.common.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageBoardExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public MessageBoardExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andStudentIdIsNull() {
            addCriterion("student_id is null");
            return (Criteria) this;
        }

        public Criteria andStudentIdIsNotNull() {
            addCriterion("student_id is not null");
            return (Criteria) this;
        }

        public Criteria andStudentIdEqualTo(Long value) {
            addCriterion("student_id =", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdNotEqualTo(Long value) {
            addCriterion("student_id <>", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdGreaterThan(Long value) {
            addCriterion("student_id >", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdGreaterThanOrEqualTo(Long value) {
            addCriterion("student_id >=", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdLessThan(Long value) {
            addCriterion("student_id <", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdLessThanOrEqualTo(Long value) {
            addCriterion("student_id <=", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdIn(List<Long> values) {
            addCriterion("student_id in", values, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdNotIn(List<Long> values) {
            addCriterion("student_id not in", values, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdBetween(Long value1, Long value2) {
            addCriterion("student_id between", value1, value2, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdNotBetween(Long value1, Long value2) {
            addCriterion("student_id not between", value1, value2, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentNameIsNull() {
            addCriterion("student_name is null");
            return (Criteria) this;
        }

        public Criteria andStudentNameIsNotNull() {
            addCriterion("student_name is not null");
            return (Criteria) this;
        }

        public Criteria andStudentNameEqualTo(String value) {
            addCriterion("student_name =", value, "studentName");
            return (Criteria) this;
        }

        public Criteria andStudentNameNotEqualTo(String value) {
            addCriterion("student_name <>", value, "studentName");
            return (Criteria) this;
        }

        public Criteria andStudentNameGreaterThan(String value) {
            addCriterion("student_name >", value, "studentName");
            return (Criteria) this;
        }

        public Criteria andStudentNameGreaterThanOrEqualTo(String value) {
            addCriterion("student_name >=", value, "studentName");
            return (Criteria) this;
        }

        public Criteria andStudentNameLessThan(String value) {
            addCriterion("student_name <", value, "studentName");
            return (Criteria) this;
        }

        public Criteria andStudentNameLessThanOrEqualTo(String value) {
            addCriterion("student_name <=", value, "studentName");
            return (Criteria) this;
        }

        public Criteria andStudentNameLike(String value) {
            addCriterion("student_name like", value, "studentName");
            return (Criteria) this;
        }

        public Criteria andStudentNameNotLike(String value) {
            addCriterion("student_name not like", value, "studentName");
            return (Criteria) this;
        }

        public Criteria andStudentNameIn(List<String> values) {
            addCriterion("student_name in", values, "studentName");
            return (Criteria) this;
        }

        public Criteria andStudentNameNotIn(List<String> values) {
            addCriterion("student_name not in", values, "studentName");
            return (Criteria) this;
        }

        public Criteria andStudentNameBetween(String value1, String value2) {
            addCriterion("student_name between", value1, value2, "studentName");
            return (Criteria) this;
        }

        public Criteria andStudentNameNotBetween(String value1, String value2) {
            addCriterion("student_name not between", value1, value2, "studentName");
            return (Criteria) this;
        }

        public Criteria andStudentAccountIsNull() {
            addCriterion("student_account is null");
            return (Criteria) this;
        }

        public Criteria andStudentAccountIsNotNull() {
            addCriterion("student_account is not null");
            return (Criteria) this;
        }

        public Criteria andStudentAccountEqualTo(String value) {
            addCriterion("student_account =", value, "studentAccount");
            return (Criteria) this;
        }

        public Criteria andStudentAccountNotEqualTo(String value) {
            addCriterion("student_account <>", value, "studentAccount");
            return (Criteria) this;
        }

        public Criteria andStudentAccountGreaterThan(String value) {
            addCriterion("student_account >", value, "studentAccount");
            return (Criteria) this;
        }

        public Criteria andStudentAccountGreaterThanOrEqualTo(String value) {
            addCriterion("student_account >=", value, "studentAccount");
            return (Criteria) this;
        }

        public Criteria andStudentAccountLessThan(String value) {
            addCriterion("student_account <", value, "studentAccount");
            return (Criteria) this;
        }

        public Criteria andStudentAccountLessThanOrEqualTo(String value) {
            addCriterion("student_account <=", value, "studentAccount");
            return (Criteria) this;
        }

        public Criteria andStudentAccountLike(String value) {
            addCriterion("student_account like", value, "studentAccount");
            return (Criteria) this;
        }

        public Criteria andStudentAccountNotLike(String value) {
            addCriterion("student_account not like", value, "studentAccount");
            return (Criteria) this;
        }

        public Criteria andStudentAccountIn(List<String> values) {
            addCriterion("student_account in", values, "studentAccount");
            return (Criteria) this;
        }

        public Criteria andStudentAccountNotIn(List<String> values) {
            addCriterion("student_account not in", values, "studentAccount");
            return (Criteria) this;
        }

        public Criteria andStudentAccountBetween(String value1, String value2) {
            addCriterion("student_account between", value1, value2, "studentAccount");
            return (Criteria) this;
        }

        public Criteria andStudentAccountNotBetween(String value1, String value2) {
            addCriterion("student_account not between", value1, value2, "studentAccount");
            return (Criteria) this;
        }

        public Criteria andSchoolNameIsNull() {
            addCriterion("school_name is null");
            return (Criteria) this;
        }

        public Criteria andSchoolNameIsNotNull() {
            addCriterion("school_name is not null");
            return (Criteria) this;
        }

        public Criteria andSchoolNameEqualTo(String value) {
            addCriterion("school_name =", value, "schoolName");
            return (Criteria) this;
        }

        public Criteria andSchoolNameNotEqualTo(String value) {
            addCriterion("school_name <>", value, "schoolName");
            return (Criteria) this;
        }

        public Criteria andSchoolNameGreaterThan(String value) {
            addCriterion("school_name >", value, "schoolName");
            return (Criteria) this;
        }

        public Criteria andSchoolNameGreaterThanOrEqualTo(String value) {
            addCriterion("school_name >=", value, "schoolName");
            return (Criteria) this;
        }

        public Criteria andSchoolNameLessThan(String value) {
            addCriterion("school_name <", value, "schoolName");
            return (Criteria) this;
        }

        public Criteria andSchoolNameLessThanOrEqualTo(String value) {
            addCriterion("school_name <=", value, "schoolName");
            return (Criteria) this;
        }

        public Criteria andSchoolNameLike(String value) {
            addCriterion("school_name like", value, "schoolName");
            return (Criteria) this;
        }

        public Criteria andSchoolNameNotLike(String value) {
            addCriterion("school_name not like", value, "schoolName");
            return (Criteria) this;
        }

        public Criteria andSchoolNameIn(List<String> values) {
            addCriterion("school_name in", values, "schoolName");
            return (Criteria) this;
        }

        public Criteria andSchoolNameNotIn(List<String> values) {
            addCriterion("school_name not in", values, "schoolName");
            return (Criteria) this;
        }

        public Criteria andSchoolNameBetween(String value1, String value2) {
            addCriterion("school_name between", value1, value2, "schoolName");
            return (Criteria) this;
        }

        public Criteria andSchoolNameNotBetween(String value1, String value2) {
            addCriterion("school_name not between", value1, value2, "schoolName");
            return (Criteria) this;
        }

        public Criteria andReplyUserIdIsNull() {
            addCriterion("reply_user_id is null");
            return (Criteria) this;
        }

        public Criteria andReplyUserIdIsNotNull() {
            addCriterion("reply_user_id is not null");
            return (Criteria) this;
        }

        public Criteria andReplyUserIdEqualTo(Long value) {
            addCriterion("reply_user_id =", value, "replyUserId");
            return (Criteria) this;
        }

        public Criteria andReplyUserIdNotEqualTo(Long value) {
            addCriterion("reply_user_id <>", value, "replyUserId");
            return (Criteria) this;
        }

        public Criteria andReplyUserIdGreaterThan(Long value) {
            addCriterion("reply_user_id >", value, "replyUserId");
            return (Criteria) this;
        }

        public Criteria andReplyUserIdGreaterThanOrEqualTo(Long value) {
            addCriterion("reply_user_id >=", value, "replyUserId");
            return (Criteria) this;
        }

        public Criteria andReplyUserIdLessThan(Long value) {
            addCriterion("reply_user_id <", value, "replyUserId");
            return (Criteria) this;
        }

        public Criteria andReplyUserIdLessThanOrEqualTo(Long value) {
            addCriterion("reply_user_id <=", value, "replyUserId");
            return (Criteria) this;
        }

        public Criteria andReplyUserIdIn(List<Long> values) {
            addCriterion("reply_user_id in", values, "replyUserId");
            return (Criteria) this;
        }

        public Criteria andReplyUserIdNotIn(List<Long> values) {
            addCriterion("reply_user_id not in", values, "replyUserId");
            return (Criteria) this;
        }

        public Criteria andReplyUserIdBetween(Long value1, Long value2) {
            addCriterion("reply_user_id between", value1, value2, "replyUserId");
            return (Criteria) this;
        }

        public Criteria andReplyUserIdNotBetween(Long value1, Long value2) {
            addCriterion("reply_user_id not between", value1, value2, "replyUserId");
            return (Criteria) this;
        }

        public Criteria andContentIsNull() {
            addCriterion("content is null");
            return (Criteria) this;
        }

        public Criteria andContentIsNotNull() {
            addCriterion("content is not null");
            return (Criteria) this;
        }

        public Criteria andContentEqualTo(String value) {
            addCriterion("content =", value, "content");
            return (Criteria) this;
        }

        public Criteria andContentNotEqualTo(String value) {
            addCriterion("content <>", value, "content");
            return (Criteria) this;
        }

        public Criteria andContentGreaterThan(String value) {
            addCriterion("content >", value, "content");
            return (Criteria) this;
        }

        public Criteria andContentGreaterThanOrEqualTo(String value) {
            addCriterion("content >=", value, "content");
            return (Criteria) this;
        }

        public Criteria andContentLessThan(String value) {
            addCriterion("content <", value, "content");
            return (Criteria) this;
        }

        public Criteria andContentLessThanOrEqualTo(String value) {
            addCriterion("content <=", value, "content");
            return (Criteria) this;
        }

        public Criteria andContentLike(String value) {
            addCriterion("content like", value, "content");
            return (Criteria) this;
        }

        public Criteria andContentNotLike(String value) {
            addCriterion("content not like", value, "content");
            return (Criteria) this;
        }

        public Criteria andContentIn(List<String> values) {
            addCriterion("content in", values, "content");
            return (Criteria) this;
        }

        public Criteria andContentNotIn(List<String> values) {
            addCriterion("content not in", values, "content");
            return (Criteria) this;
        }

        public Criteria andContentBetween(String value1, String value2) {
            addCriterion("content between", value1, value2, "content");
            return (Criteria) this;
        }

        public Criteria andContentNotBetween(String value1, String value2) {
            addCriterion("content not between", value1, value2, "content");
            return (Criteria) this;
        }

        public Criteria andTimeIsNull() {
            addCriterion("time is null");
            return (Criteria) this;
        }

        public Criteria andTimeIsNotNull() {
            addCriterion("time is not null");
            return (Criteria) this;
        }

        public Criteria andTimeEqualTo(Date value) {
            addCriterion("time =", value, "time");
            return (Criteria) this;
        }

        public Criteria andTimeNotEqualTo(Date value) {
            addCriterion("time <>", value, "time");
            return (Criteria) this;
        }

        public Criteria andTimeGreaterThan(Date value) {
            addCriterion("time >", value, "time");
            return (Criteria) this;
        }

        public Criteria andTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("time >=", value, "time");
            return (Criteria) this;
        }

        public Criteria andTimeLessThan(Date value) {
            addCriterion("time <", value, "time");
            return (Criteria) this;
        }

        public Criteria andTimeLessThanOrEqualTo(Date value) {
            addCriterion("time <=", value, "time");
            return (Criteria) this;
        }

        public Criteria andTimeIn(List<Date> values) {
            addCriterion("time in", values, "time");
            return (Criteria) this;
        }

        public Criteria andTimeNotIn(List<Date> values) {
            addCriterion("time not in", values, "time");
            return (Criteria) this;
        }

        public Criteria andTimeBetween(Date value1, Date value2) {
            addCriterion("time between", value1, value2, "time");
            return (Criteria) this;
        }

        public Criteria andTimeNotBetween(Date value1, Date value2) {
            addCriterion("time not between", value1, value2, "time");
            return (Criteria) this;
        }

        public Criteria andReadFlagIsNull() {
            addCriterion("read_flag is null");
            return (Criteria) this;
        }

        public Criteria andReadFlagIsNotNull() {
            addCriterion("read_flag is not null");
            return (Criteria) this;
        }

        public Criteria andReadFlagEqualTo(Integer value) {
            addCriterion("read_flag =", value, "readFlag");
            return (Criteria) this;
        }

        public Criteria andReadFlagNotEqualTo(Integer value) {
            addCriterion("read_flag <>", value, "readFlag");
            return (Criteria) this;
        }

        public Criteria andReadFlagGreaterThan(Integer value) {
            addCriterion("read_flag >", value, "readFlag");
            return (Criteria) this;
        }

        public Criteria andReadFlagGreaterThanOrEqualTo(Integer value) {
            addCriterion("read_flag >=", value, "readFlag");
            return (Criteria) this;
        }

        public Criteria andReadFlagLessThan(Integer value) {
            addCriterion("read_flag <", value, "readFlag");
            return (Criteria) this;
        }

        public Criteria andReadFlagLessThanOrEqualTo(Integer value) {
            addCriterion("read_flag <=", value, "readFlag");
            return (Criteria) this;
        }

        public Criteria andReadFlagIn(List<Integer> values) {
            addCriterion("read_flag in", values, "readFlag");
            return (Criteria) this;
        }

        public Criteria andReadFlagNotIn(List<Integer> values) {
            addCriterion("read_flag not in", values, "readFlag");
            return (Criteria) this;
        }

        public Criteria andReadFlagBetween(Integer value1, Integer value2) {
            addCriterion("read_flag between", value1, value2, "readFlag");
            return (Criteria) this;
        }

        public Criteria andReadFlagNotBetween(Integer value1, Integer value2) {
            addCriterion("read_flag not between", value1, value2, "readFlag");
            return (Criteria) this;
        }

        public Criteria andAcceptFlagIsNull() {
            addCriterion("accept_flag is null");
            return (Criteria) this;
        }

        public Criteria andAcceptFlagIsNotNull() {
            addCriterion("accept_flag is not null");
            return (Criteria) this;
        }

        public Criteria andAcceptFlagEqualTo(Integer value) {
            addCriterion("accept_flag =", value, "acceptFlag");
            return (Criteria) this;
        }

        public Criteria andAcceptFlagNotEqualTo(Integer value) {
            addCriterion("accept_flag <>", value, "acceptFlag");
            return (Criteria) this;
        }

        public Criteria andAcceptFlagGreaterThan(Integer value) {
            addCriterion("accept_flag >", value, "acceptFlag");
            return (Criteria) this;
        }

        public Criteria andAcceptFlagGreaterThanOrEqualTo(Integer value) {
            addCriterion("accept_flag >=", value, "acceptFlag");
            return (Criteria) this;
        }

        public Criteria andAcceptFlagLessThan(Integer value) {
            addCriterion("accept_flag <", value, "acceptFlag");
            return (Criteria) this;
        }

        public Criteria andAcceptFlagLessThanOrEqualTo(Integer value) {
            addCriterion("accept_flag <=", value, "acceptFlag");
            return (Criteria) this;
        }

        public Criteria andAcceptFlagIn(List<Integer> values) {
            addCriterion("accept_flag in", values, "acceptFlag");
            return (Criteria) this;
        }

        public Criteria andAcceptFlagNotIn(List<Integer> values) {
            addCriterion("accept_flag not in", values, "acceptFlag");
            return (Criteria) this;
        }

        public Criteria andAcceptFlagBetween(Integer value1, Integer value2) {
            addCriterion("accept_flag between", value1, value2, "acceptFlag");
            return (Criteria) this;
        }

        public Criteria andAcceptFlagNotBetween(Integer value1, Integer value2) {
            addCriterion("accept_flag not between", value1, value2, "acceptFlag");
            return (Criteria) this;
        }

        public Criteria andAwardGoldIsNull() {
            addCriterion("award_gold is null");
            return (Criteria) this;
        }

        public Criteria andAwardGoldIsNotNull() {
            addCriterion("award_gold is not null");
            return (Criteria) this;
        }

        public Criteria andAwardGoldEqualTo(Integer value) {
            addCriterion("award_gold =", value, "awardGold");
            return (Criteria) this;
        }

        public Criteria andAwardGoldNotEqualTo(Integer value) {
            addCriterion("award_gold <>", value, "awardGold");
            return (Criteria) this;
        }

        public Criteria andAwardGoldGreaterThan(Integer value) {
            addCriterion("award_gold >", value, "awardGold");
            return (Criteria) this;
        }

        public Criteria andAwardGoldGreaterThanOrEqualTo(Integer value) {
            addCriterion("award_gold >=", value, "awardGold");
            return (Criteria) this;
        }

        public Criteria andAwardGoldLessThan(Integer value) {
            addCriterion("award_gold <", value, "awardGold");
            return (Criteria) this;
        }

        public Criteria andAwardGoldLessThanOrEqualTo(Integer value) {
            addCriterion("award_gold <=", value, "awardGold");
            return (Criteria) this;
        }

        public Criteria andAwardGoldIn(List<Integer> values) {
            addCriterion("award_gold in", values, "awardGold");
            return (Criteria) this;
        }

        public Criteria andAwardGoldNotIn(List<Integer> values) {
            addCriterion("award_gold not in", values, "awardGold");
            return (Criteria) this;
        }

        public Criteria andAwardGoldBetween(Integer value1, Integer value2) {
            addCriterion("award_gold between", value1, value2, "awardGold");
            return (Criteria) this;
        }

        public Criteria andAwardGoldNotBetween(Integer value1, Integer value2) {
            addCriterion("award_gold not between", value1, value2, "awardGold");
            return (Criteria) this;
        }

        public Criteria andHintFlagIsNull() {
            addCriterion("hint_flag is null");
            return (Criteria) this;
        }

        public Criteria andHintFlagIsNotNull() {
            addCriterion("hint_flag is not null");
            return (Criteria) this;
        }

        public Criteria andHintFlagEqualTo(Integer value) {
            addCriterion("hint_flag =", value, "hintFlag");
            return (Criteria) this;
        }

        public Criteria andHintFlagNotEqualTo(Integer value) {
            addCriterion("hint_flag <>", value, "hintFlag");
            return (Criteria) this;
        }

        public Criteria andHintFlagGreaterThan(Integer value) {
            addCriterion("hint_flag >", value, "hintFlag");
            return (Criteria) this;
        }

        public Criteria andHintFlagGreaterThanOrEqualTo(Integer value) {
            addCriterion("hint_flag >=", value, "hintFlag");
            return (Criteria) this;
        }

        public Criteria andHintFlagLessThan(Integer value) {
            addCriterion("hint_flag <", value, "hintFlag");
            return (Criteria) this;
        }

        public Criteria andHintFlagLessThanOrEqualTo(Integer value) {
            addCriterion("hint_flag <=", value, "hintFlag");
            return (Criteria) this;
        }

        public Criteria andHintFlagIn(List<Integer> values) {
            addCriterion("hint_flag in", values, "hintFlag");
            return (Criteria) this;
        }

        public Criteria andHintFlagNotIn(List<Integer> values) {
            addCriterion("hint_flag not in", values, "hintFlag");
            return (Criteria) this;
        }

        public Criteria andHintFlagBetween(Integer value1, Integer value2) {
            addCriterion("hint_flag between", value1, value2, "hintFlag");
            return (Criteria) this;
        }

        public Criteria andHintFlagNotBetween(Integer value1, Integer value2) {
            addCriterion("hint_flag not between", value1, value2, "hintFlag");
            return (Criteria) this;
        }

        public Criteria andRoleIsNull() {
            addCriterion("role is null");
            return (Criteria) this;
        }

        public Criteria andRoleIsNotNull() {
            addCriterion("role is not null");
            return (Criteria) this;
        }

        public Criteria andRoleEqualTo(Integer value) {
            addCriterion("role =", value, "role");
            return (Criteria) this;
        }

        public Criteria andRoleNotEqualTo(Integer value) {
            addCriterion("role <>", value, "role");
            return (Criteria) this;
        }

        public Criteria andRoleGreaterThan(Integer value) {
            addCriterion("role >", value, "role");
            return (Criteria) this;
        }

        public Criteria andRoleGreaterThanOrEqualTo(Integer value) {
            addCriterion("role >=", value, "role");
            return (Criteria) this;
        }

        public Criteria andRoleLessThan(Integer value) {
            addCriterion("role <", value, "role");
            return (Criteria) this;
        }

        public Criteria andRoleLessThanOrEqualTo(Integer value) {
            addCriterion("role <=", value, "role");
            return (Criteria) this;
        }

        public Criteria andRoleIn(List<Integer> values) {
            addCriterion("role in", values, "role");
            return (Criteria) this;
        }

        public Criteria andRoleNotIn(List<Integer> values) {
            addCriterion("role not in", values, "role");
            return (Criteria) this;
        }

        public Criteria andRoleBetween(Integer value1, Integer value2) {
            addCriterion("role between", value1, value2, "role");
            return (Criteria) this;
        }

        public Criteria andRoleNotBetween(Integer value1, Integer value2) {
            addCriterion("role not between", value1, value2, "role");
            return (Criteria) this;
        }

        public Criteria andStopSpeakEndTimeIsNull() {
            addCriterion("stop_speak_end_time is null");
            return (Criteria) this;
        }

        public Criteria andStopSpeakEndTimeIsNotNull() {
            addCriterion("stop_speak_end_time is not null");
            return (Criteria) this;
        }

        public Criteria andStopSpeakEndTimeEqualTo(Date value) {
            addCriterion("stop_speak_end_time =", value, "stopSpeakEndTime");
            return (Criteria) this;
        }

        public Criteria andStopSpeakEndTimeNotEqualTo(Date value) {
            addCriterion("stop_speak_end_time <>", value, "stopSpeakEndTime");
            return (Criteria) this;
        }

        public Criteria andStopSpeakEndTimeGreaterThan(Date value) {
            addCriterion("stop_speak_end_time >", value, "stopSpeakEndTime");
            return (Criteria) this;
        }

        public Criteria andStopSpeakEndTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("stop_speak_end_time >=", value, "stopSpeakEndTime");
            return (Criteria) this;
        }

        public Criteria andStopSpeakEndTimeLessThan(Date value) {
            addCriterion("stop_speak_end_time <", value, "stopSpeakEndTime");
            return (Criteria) this;
        }

        public Criteria andStopSpeakEndTimeLessThanOrEqualTo(Date value) {
            addCriterion("stop_speak_end_time <=", value, "stopSpeakEndTime");
            return (Criteria) this;
        }

        public Criteria andStopSpeakEndTimeIn(List<Date> values) {
            addCriterion("stop_speak_end_time in", values, "stopSpeakEndTime");
            return (Criteria) this;
        }

        public Criteria andStopSpeakEndTimeNotIn(List<Date> values) {
            addCriterion("stop_speak_end_time not in", values, "stopSpeakEndTime");
            return (Criteria) this;
        }

        public Criteria andStopSpeakEndTimeBetween(Date value1, Date value2) {
            addCriterion("stop_speak_end_time between", value1, value2, "stopSpeakEndTime");
            return (Criteria) this;
        }

        public Criteria andStopSpeakEndTimeNotBetween(Date value1, Date value2) {
            addCriterion("stop_speak_end_time not between", value1, value2, "stopSpeakEndTime");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}