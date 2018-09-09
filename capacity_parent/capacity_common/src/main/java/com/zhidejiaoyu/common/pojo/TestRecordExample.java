package com.zhidejiaoyu.common.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestRecordExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public TestRecordExample() {
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

        public Criteria andCourseIdIsNull() {
            addCriterion("course_id is null");
            return (Criteria) this;
        }

        public Criteria andCourseIdIsNotNull() {
            addCriterion("course_id is not null");
            return (Criteria) this;
        }

        public Criteria andCourseIdEqualTo(Long value) {
            addCriterion("course_id =", value, "courseId");
            return (Criteria) this;
        }

        public Criteria andCourseIdNotEqualTo(Long value) {
            addCriterion("course_id <>", value, "courseId");
            return (Criteria) this;
        }

        public Criteria andCourseIdGreaterThan(Long value) {
            addCriterion("course_id >", value, "courseId");
            return (Criteria) this;
        }

        public Criteria andCourseIdGreaterThanOrEqualTo(Long value) {
            addCriterion("course_id >=", value, "courseId");
            return (Criteria) this;
        }

        public Criteria andCourseIdLessThan(Long value) {
            addCriterion("course_id <", value, "courseId");
            return (Criteria) this;
        }

        public Criteria andCourseIdLessThanOrEqualTo(Long value) {
            addCriterion("course_id <=", value, "courseId");
            return (Criteria) this;
        }

        public Criteria andCourseIdIn(List<Long> values) {
            addCriterion("course_id in", values, "courseId");
            return (Criteria) this;
        }

        public Criteria andCourseIdNotIn(List<Long> values) {
            addCriterion("course_id not in", values, "courseId");
            return (Criteria) this;
        }

        public Criteria andCourseIdBetween(Long value1, Long value2) {
            addCriterion("course_id between", value1, value2, "courseId");
            return (Criteria) this;
        }

        public Criteria andCourseIdNotBetween(Long value1, Long value2) {
            addCriterion("course_id not between", value1, value2, "courseId");
            return (Criteria) this;
        }

        public Criteria andUnitIdIsNull() {
            addCriterion("unit_id is null");
            return (Criteria) this;
        }

        public Criteria andUnitIdIsNotNull() {
            addCriterion("unit_id is not null");
            return (Criteria) this;
        }

        public Criteria andUnitIdEqualTo(Long value) {
            addCriterion("unit_id =", value, "unitId");
            return (Criteria) this;
        }

        public Criteria andUnitIdNotEqualTo(Long value) {
            addCriterion("unit_id <>", value, "unitId");
            return (Criteria) this;
        }

        public Criteria andUnitIdGreaterThan(Long value) {
            addCriterion("unit_id >", value, "unitId");
            return (Criteria) this;
        }

        public Criteria andUnitIdGreaterThanOrEqualTo(Long value) {
            addCriterion("unit_id >=", value, "unitId");
            return (Criteria) this;
        }

        public Criteria andUnitIdLessThan(Long value) {
            addCriterion("unit_id <", value, "unitId");
            return (Criteria) this;
        }

        public Criteria andUnitIdLessThanOrEqualTo(Long value) {
            addCriterion("unit_id <=", value, "unitId");
            return (Criteria) this;
        }

        public Criteria andUnitIdIn(List<Long> values) {
            addCriterion("unit_id in", values, "unitId");
            return (Criteria) this;
        }

        public Criteria andUnitIdNotIn(List<Long> values) {
            addCriterion("unit_id not in", values, "unitId");
            return (Criteria) this;
        }

        public Criteria andUnitIdBetween(Long value1, Long value2) {
            addCriterion("unit_id between", value1, value2, "unitId");
            return (Criteria) this;
        }

        public Criteria andUnitIdNotBetween(Long value1, Long value2) {
            addCriterion("unit_id not between", value1, value2, "unitId");
            return (Criteria) this;
        }

        public Criteria andGenreIsNull() {
            addCriterion("genre is null");
            return (Criteria) this;
        }

        public Criteria andGenreIsNotNull() {
            addCriterion("genre is not null");
            return (Criteria) this;
        }

        public Criteria andGenreEqualTo(String value) {
            addCriterion("genre =", value, "genre");
            return (Criteria) this;
        }

        public Criteria andGenreNotEqualTo(String value) {
            addCriterion("genre <>", value, "genre");
            return (Criteria) this;
        }

        public Criteria andGenreGreaterThan(String value) {
            addCriterion("genre >", value, "genre");
            return (Criteria) this;
        }

        public Criteria andGenreGreaterThanOrEqualTo(String value) {
            addCriterion("genre >=", value, "genre");
            return (Criteria) this;
        }

        public Criteria andGenreLessThan(String value) {
            addCriterion("genre <", value, "genre");
            return (Criteria) this;
        }

        public Criteria andGenreLessThanOrEqualTo(String value) {
            addCriterion("genre <=", value, "genre");
            return (Criteria) this;
        }

        public Criteria andGenreLike(String value) {
            addCriterion("genre like", value, "genre");
            return (Criteria) this;
        }

        public Criteria andGenreNotLike(String value) {
            addCriterion("genre not like", value, "genre");
            return (Criteria) this;
        }

        public Criteria andGenreIn(List<String> values) {
            addCriterion("genre in", values, "genre");
            return (Criteria) this;
        }

        public Criteria andGenreNotIn(List<String> values) {
            addCriterion("genre not in", values, "genre");
            return (Criteria) this;
        }

        public Criteria andGenreBetween(String value1, String value2) {
            addCriterion("genre between", value1, value2, "genre");
            return (Criteria) this;
        }

        public Criteria andGenreNotBetween(String value1, String value2) {
            addCriterion("genre not between", value1, value2, "genre");
            return (Criteria) this;
        }

        public Criteria andTestStartTimeIsNull() {
            addCriterion("test_start_time is null");
            return (Criteria) this;
        }

        public Criteria andTestStartTimeIsNotNull() {
            addCriterion("test_start_time is not null");
            return (Criteria) this;
        }

        public Criteria andTestStartTimeEqualTo(Date value) {
            addCriterion("test_start_time =", value, "testStartTime");
            return (Criteria) this;
        }

        public Criteria andTestStartTimeNotEqualTo(Date value) {
            addCriterion("test_start_time <>", value, "testStartTime");
            return (Criteria) this;
        }

        public Criteria andTestStartTimeGreaterThan(Date value) {
            addCriterion("test_start_time >", value, "testStartTime");
            return (Criteria) this;
        }

        public Criteria andTestStartTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("test_start_time >=", value, "testStartTime");
            return (Criteria) this;
        }

        public Criteria andTestStartTimeLessThan(Date value) {
            addCriterion("test_start_time <", value, "testStartTime");
            return (Criteria) this;
        }

        public Criteria andTestStartTimeLessThanOrEqualTo(Date value) {
            addCriterion("test_start_time <=", value, "testStartTime");
            return (Criteria) this;
        }

        public Criteria andTestStartTimeIn(List<Date> values) {
            addCriterion("test_start_time in", values, "testStartTime");
            return (Criteria) this;
        }

        public Criteria andTestStartTimeNotIn(List<Date> values) {
            addCriterion("test_start_time not in", values, "testStartTime");
            return (Criteria) this;
        }

        public Criteria andTestStartTimeBetween(Date value1, Date value2) {
            addCriterion("test_start_time between", value1, value2, "testStartTime");
            return (Criteria) this;
        }

        public Criteria andTestStartTimeNotBetween(Date value1, Date value2) {
            addCriterion("test_start_time not between", value1, value2, "testStartTime");
            return (Criteria) this;
        }

        public Criteria andTestEndTimeIsNull() {
            addCriterion("test_end_time is null");
            return (Criteria) this;
        }

        public Criteria andTestEndTimeIsNotNull() {
            addCriterion("test_end_time is not null");
            return (Criteria) this;
        }

        public Criteria andTestEndTimeEqualTo(Date value) {
            addCriterion("test_end_time =", value, "testEndTime");
            return (Criteria) this;
        }

        public Criteria andTestEndTimeNotEqualTo(Date value) {
            addCriterion("test_end_time <>", value, "testEndTime");
            return (Criteria) this;
        }

        public Criteria andTestEndTimeGreaterThan(Date value) {
            addCriterion("test_end_time >", value, "testEndTime");
            return (Criteria) this;
        }

        public Criteria andTestEndTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("test_end_time >=", value, "testEndTime");
            return (Criteria) this;
        }

        public Criteria andTestEndTimeLessThan(Date value) {
            addCriterion("test_end_time <", value, "testEndTime");
            return (Criteria) this;
        }

        public Criteria andTestEndTimeLessThanOrEqualTo(Date value) {
            addCriterion("test_end_time <=", value, "testEndTime");
            return (Criteria) this;
        }

        public Criteria andTestEndTimeIn(List<Date> values) {
            addCriterion("test_end_time in", values, "testEndTime");
            return (Criteria) this;
        }

        public Criteria andTestEndTimeNotIn(List<Date> values) {
            addCriterion("test_end_time not in", values, "testEndTime");
            return (Criteria) this;
        }

        public Criteria andTestEndTimeBetween(Date value1, Date value2) {
            addCriterion("test_end_time between", value1, value2, "testEndTime");
            return (Criteria) this;
        }

        public Criteria andTestEndTimeNotBetween(Date value1, Date value2) {
            addCriterion("test_end_time not between", value1, value2, "testEndTime");
            return (Criteria) this;
        }

        public Criteria andPointIsNull() {
            addCriterion("point is null");
            return (Criteria) this;
        }

        public Criteria andPointIsNotNull() {
            addCriterion("point is not null");
            return (Criteria) this;
        }

        public Criteria andPointEqualTo(Integer value) {
            addCriterion("point =", value, "point");
            return (Criteria) this;
        }

        public Criteria andPointNotEqualTo(Integer value) {
            addCriterion("point <>", value, "point");
            return (Criteria) this;
        }

        public Criteria andPointGreaterThan(Integer value) {
            addCriterion("point >", value, "point");
            return (Criteria) this;
        }

        public Criteria andPointGreaterThanOrEqualTo(Integer value) {
            addCriterion("point >=", value, "point");
            return (Criteria) this;
        }

        public Criteria andPointLessThan(Integer value) {
            addCriterion("point <", value, "point");
            return (Criteria) this;
        }

        public Criteria andPointLessThanOrEqualTo(Integer value) {
            addCriterion("point <=", value, "point");
            return (Criteria) this;
        }

        public Criteria andPointIn(List<Integer> values) {
            addCriterion("point in", values, "point");
            return (Criteria) this;
        }

        public Criteria andPointNotIn(List<Integer> values) {
            addCriterion("point not in", values, "point");
            return (Criteria) this;
        }

        public Criteria andPointBetween(Integer value1, Integer value2) {
            addCriterion("point between", value1, value2, "point");
            return (Criteria) this;
        }

        public Criteria andPointNotBetween(Integer value1, Integer value2) {
            addCriterion("point not between", value1, value2, "point");
            return (Criteria) this;
        }

        public Criteria andHistoryBestPointIsNull() {
            addCriterion("history_best_point is null");
            return (Criteria) this;
        }

        public Criteria andHistoryBestPointIsNotNull() {
            addCriterion("history_best_point is not null");
            return (Criteria) this;
        }

        public Criteria andHistoryBestPointEqualTo(Integer value) {
            addCriterion("history_best_point =", value, "historyBestPoint");
            return (Criteria) this;
        }

        public Criteria andHistoryBestPointNotEqualTo(Integer value) {
            addCriterion("history_best_point <>", value, "historyBestPoint");
            return (Criteria) this;
        }

        public Criteria andHistoryBestPointGreaterThan(Integer value) {
            addCriterion("history_best_point >", value, "historyBestPoint");
            return (Criteria) this;
        }

        public Criteria andHistoryBestPointGreaterThanOrEqualTo(Integer value) {
            addCriterion("history_best_point >=", value, "historyBestPoint");
            return (Criteria) this;
        }

        public Criteria andHistoryBestPointLessThan(Integer value) {
            addCriterion("history_best_point <", value, "historyBestPoint");
            return (Criteria) this;
        }

        public Criteria andHistoryBestPointLessThanOrEqualTo(Integer value) {
            addCriterion("history_best_point <=", value, "historyBestPoint");
            return (Criteria) this;
        }

        public Criteria andHistoryBestPointIn(List<Integer> values) {
            addCriterion("history_best_point in", values, "historyBestPoint");
            return (Criteria) this;
        }

        public Criteria andHistoryBestPointNotIn(List<Integer> values) {
            addCriterion("history_best_point not in", values, "historyBestPoint");
            return (Criteria) this;
        }

        public Criteria andHistoryBestPointBetween(Integer value1, Integer value2) {
            addCriterion("history_best_point between", value1, value2, "historyBestPoint");
            return (Criteria) this;
        }

        public Criteria andHistoryBestPointNotBetween(Integer value1, Integer value2) {
            addCriterion("history_best_point not between", value1, value2, "historyBestPoint");
            return (Criteria) this;
        }

        public Criteria andHistoryBadPointIsNull() {
            addCriterion("history_bad_point is null");
            return (Criteria) this;
        }

        public Criteria andHistoryBadPointIsNotNull() {
            addCriterion("history_bad_point is not null");
            return (Criteria) this;
        }

        public Criteria andHistoryBadPointEqualTo(Integer value) {
            addCriterion("history_bad_point =", value, "historyBadPoint");
            return (Criteria) this;
        }

        public Criteria andHistoryBadPointNotEqualTo(Integer value) {
            addCriterion("history_bad_point <>", value, "historyBadPoint");
            return (Criteria) this;
        }

        public Criteria andHistoryBadPointGreaterThan(Integer value) {
            addCriterion("history_bad_point >", value, "historyBadPoint");
            return (Criteria) this;
        }

        public Criteria andHistoryBadPointGreaterThanOrEqualTo(Integer value) {
            addCriterion("history_bad_point >=", value, "historyBadPoint");
            return (Criteria) this;
        }

        public Criteria andHistoryBadPointLessThan(Integer value) {
            addCriterion("history_bad_point <", value, "historyBadPoint");
            return (Criteria) this;
        }

        public Criteria andHistoryBadPointLessThanOrEqualTo(Integer value) {
            addCriterion("history_bad_point <=", value, "historyBadPoint");
            return (Criteria) this;
        }

        public Criteria andHistoryBadPointIn(List<Integer> values) {
            addCriterion("history_bad_point in", values, "historyBadPoint");
            return (Criteria) this;
        }

        public Criteria andHistoryBadPointNotIn(List<Integer> values) {
            addCriterion("history_bad_point not in", values, "historyBadPoint");
            return (Criteria) this;
        }

        public Criteria andHistoryBadPointBetween(Integer value1, Integer value2) {
            addCriterion("history_bad_point between", value1, value2, "historyBadPoint");
            return (Criteria) this;
        }

        public Criteria andHistoryBadPointNotBetween(Integer value1, Integer value2) {
            addCriterion("history_bad_point not between", value1, value2, "historyBadPoint");
            return (Criteria) this;
        }

        public Criteria andQuantityIsNull() {
            addCriterion("quantity is null");
            return (Criteria) this;
        }

        public Criteria andQuantityIsNotNull() {
            addCriterion("quantity is not null");
            return (Criteria) this;
        }

        public Criteria andQuantityEqualTo(Integer value) {
            addCriterion("quantity =", value, "quantity");
            return (Criteria) this;
        }

        public Criteria andQuantityNotEqualTo(Integer value) {
            addCriterion("quantity <>", value, "quantity");
            return (Criteria) this;
        }

        public Criteria andQuantityGreaterThan(Integer value) {
            addCriterion("quantity >", value, "quantity");
            return (Criteria) this;
        }

        public Criteria andQuantityGreaterThanOrEqualTo(Integer value) {
            addCriterion("quantity >=", value, "quantity");
            return (Criteria) this;
        }

        public Criteria andQuantityLessThan(Integer value) {
            addCriterion("quantity <", value, "quantity");
            return (Criteria) this;
        }

        public Criteria andQuantityLessThanOrEqualTo(Integer value) {
            addCriterion("quantity <=", value, "quantity");
            return (Criteria) this;
        }

        public Criteria andQuantityIn(List<Integer> values) {
            addCriterion("quantity in", values, "quantity");
            return (Criteria) this;
        }

        public Criteria andQuantityNotIn(List<Integer> values) {
            addCriterion("quantity not in", values, "quantity");
            return (Criteria) this;
        }

        public Criteria andQuantityBetween(Integer value1, Integer value2) {
            addCriterion("quantity between", value1, value2, "quantity");
            return (Criteria) this;
        }

        public Criteria andQuantityNotBetween(Integer value1, Integer value2) {
            addCriterion("quantity not between", value1, value2, "quantity");
            return (Criteria) this;
        }

        public Criteria andExplainIsNull() {
            addCriterion("explain is null");
            return (Criteria) this;
        }

        public Criteria andExplainIsNotNull() {
            addCriterion("explain is not null");
            return (Criteria) this;
        }

        public Criteria andExplainEqualTo(String value) {
            addCriterion("explain =", value, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainNotEqualTo(String value) {
            addCriterion("explain <>", value, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainGreaterThan(String value) {
            addCriterion("explain >", value, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainGreaterThanOrEqualTo(String value) {
            addCriterion("explain >=", value, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainLessThan(String value) {
            addCriterion("explain <", value, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainLessThanOrEqualTo(String value) {
            addCriterion("explain <=", value, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainLike(String value) {
            addCriterion("explain like", value, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainNotLike(String value) {
            addCriterion("explain not like", value, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainIn(List<String> values) {
            addCriterion("explain in", values, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainNotIn(List<String> values) {
            addCriterion("explain not in", values, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainBetween(String value1, String value2) {
            addCriterion("explain between", value1, value2, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainNotBetween(String value1, String value2) {
            addCriterion("explain not between", value1, value2, "explain");
            return (Criteria) this;
        }

        public Criteria andErrorCountIsNull() {
            addCriterion("error_count is null");
            return (Criteria) this;
        }

        public Criteria andErrorCountIsNotNull() {
            addCriterion("error_count is not null");
            return (Criteria) this;
        }

        public Criteria andErrorCountEqualTo(Integer value) {
            addCriterion("error_count =", value, "errorCount");
            return (Criteria) this;
        }

        public Criteria andErrorCountNotEqualTo(Integer value) {
            addCriterion("error_count <>", value, "errorCount");
            return (Criteria) this;
        }

        public Criteria andErrorCountGreaterThan(Integer value) {
            addCriterion("error_count >", value, "errorCount");
            return (Criteria) this;
        }

        public Criteria andErrorCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("error_count >=", value, "errorCount");
            return (Criteria) this;
        }

        public Criteria andErrorCountLessThan(Integer value) {
            addCriterion("error_count <", value, "errorCount");
            return (Criteria) this;
        }

        public Criteria andErrorCountLessThanOrEqualTo(Integer value) {
            addCriterion("error_count <=", value, "errorCount");
            return (Criteria) this;
        }

        public Criteria andErrorCountIn(List<Integer> values) {
            addCriterion("error_count in", values, "errorCount");
            return (Criteria) this;
        }

        public Criteria andErrorCountNotIn(List<Integer> values) {
            addCriterion("error_count not in", values, "errorCount");
            return (Criteria) this;
        }

        public Criteria andErrorCountBetween(Integer value1, Integer value2) {
            addCriterion("error_count between", value1, value2, "errorCount");
            return (Criteria) this;
        }

        public Criteria andErrorCountNotBetween(Integer value1, Integer value2) {
            addCriterion("error_count not between", value1, value2, "errorCount");
            return (Criteria) this;
        }

        public Criteria andRightCountIsNull() {
            addCriterion("right_count is null");
            return (Criteria) this;
        }

        public Criteria andRightCountIsNotNull() {
            addCriterion("right_count is not null");
            return (Criteria) this;
        }

        public Criteria andRightCountEqualTo(Integer value) {
            addCriterion("right_count =", value, "rightCount");
            return (Criteria) this;
        }

        public Criteria andRightCountNotEqualTo(Integer value) {
            addCriterion("right_count <>", value, "rightCount");
            return (Criteria) this;
        }

        public Criteria andRightCountGreaterThan(Integer value) {
            addCriterion("right_count >", value, "rightCount");
            return (Criteria) this;
        }

        public Criteria andRightCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("right_count >=", value, "rightCount");
            return (Criteria) this;
        }

        public Criteria andRightCountLessThan(Integer value) {
            addCriterion("right_count <", value, "rightCount");
            return (Criteria) this;
        }

        public Criteria andRightCountLessThanOrEqualTo(Integer value) {
            addCriterion("right_count <=", value, "rightCount");
            return (Criteria) this;
        }

        public Criteria andRightCountIn(List<Integer> values) {
            addCriterion("right_count in", values, "rightCount");
            return (Criteria) this;
        }

        public Criteria andRightCountNotIn(List<Integer> values) {
            addCriterion("right_count not in", values, "rightCount");
            return (Criteria) this;
        }

        public Criteria andRightCountBetween(Integer value1, Integer value2) {
            addCriterion("right_count between", value1, value2, "rightCount");
            return (Criteria) this;
        }

        public Criteria andRightCountNotBetween(Integer value1, Integer value2) {
            addCriterion("right_count not between", value1, value2, "rightCount");
            return (Criteria) this;
        }

        public Criteria andStudyModelIsNull() {
            addCriterion("study_model is null");
            return (Criteria) this;
        }

        public Criteria andStudyModelIsNotNull() {
            addCriterion("study_model is not null");
            return (Criteria) this;
        }

        public Criteria andStudyModelEqualTo(String value) {
            addCriterion("study_model =", value, "studyModel");
            return (Criteria) this;
        }

        public Criteria andStudyModelNotEqualTo(String value) {
            addCriterion("study_model <>", value, "studyModel");
            return (Criteria) this;
        }

        public Criteria andStudyModelGreaterThan(String value) {
            addCriterion("study_model >", value, "studyModel");
            return (Criteria) this;
        }

        public Criteria andStudyModelGreaterThanOrEqualTo(String value) {
            addCriterion("study_model >=", value, "studyModel");
            return (Criteria) this;
        }

        public Criteria andStudyModelLessThan(String value) {
            addCriterion("study_model <", value, "studyModel");
            return (Criteria) this;
        }

        public Criteria andStudyModelLessThanOrEqualTo(String value) {
            addCriterion("study_model <=", value, "studyModel");
            return (Criteria) this;
        }

        public Criteria andStudyModelLike(String value) {
            addCriterion("study_model like", value, "studyModel");
            return (Criteria) this;
        }

        public Criteria andStudyModelNotLike(String value) {
            addCriterion("study_model not like", value, "studyModel");
            return (Criteria) this;
        }

        public Criteria andStudyModelIn(List<String> values) {
            addCriterion("study_model in", values, "studyModel");
            return (Criteria) this;
        }

        public Criteria andStudyModelNotIn(List<String> values) {
            addCriterion("study_model not in", values, "studyModel");
            return (Criteria) this;
        }

        public Criteria andStudyModelBetween(String value1, String value2) {
            addCriterion("study_model between", value1, value2, "studyModel");
            return (Criteria) this;
        }

        public Criteria andStudyModelNotBetween(String value1, String value2) {
            addCriterion("study_model not between", value1, value2, "studyModel");
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

        public Criteria andBetterCountIsNull() {
            addCriterion("better_count is null");
            return (Criteria) this;
        }

        public Criteria andBetterCountIsNotNull() {
            addCriterion("better_count is not null");
            return (Criteria) this;
        }

        public Criteria andBetterCountEqualTo(Integer value) {
            addCriterion("better_count =", value, "betterCount");
            return (Criteria) this;
        }

        public Criteria andBetterCountNotEqualTo(Integer value) {
            addCriterion("better_count <>", value, "betterCount");
            return (Criteria) this;
        }

        public Criteria andBetterCountGreaterThan(Integer value) {
            addCriterion("better_count >", value, "betterCount");
            return (Criteria) this;
        }

        public Criteria andBetterCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("better_count >=", value, "betterCount");
            return (Criteria) this;
        }

        public Criteria andBetterCountLessThan(Integer value) {
            addCriterion("better_count <", value, "betterCount");
            return (Criteria) this;
        }

        public Criteria andBetterCountLessThanOrEqualTo(Integer value) {
            addCriterion("better_count <=", value, "betterCount");
            return (Criteria) this;
        }

        public Criteria andBetterCountIn(List<Integer> values) {
            addCriterion("better_count in", values, "betterCount");
            return (Criteria) this;
        }

        public Criteria andBetterCountNotIn(List<Integer> values) {
            addCriterion("better_count not in", values, "betterCount");
            return (Criteria) this;
        }

        public Criteria andBetterCountBetween(Integer value1, Integer value2) {
            addCriterion("better_count between", value1, value2, "betterCount");
            return (Criteria) this;
        }

        public Criteria andBetterCountNotBetween(Integer value1, Integer value2) {
            addCriterion("better_count not between", value1, value2, "betterCount");
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