package com.zhidejiaoyu.common.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LearnExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public LearnExample() {
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

        public Criteria andVocabularyIdIsNull() {
            addCriterion("vocabulary_id is null");
            return (Criteria) this;
        }

        public Criteria andVocabularyIdIsNotNull() {
            addCriterion("vocabulary_id is not null");
            return (Criteria) this;
        }

        public Criteria andVocabularyIdEqualTo(Long value) {
            addCriterion("vocabulary_id =", value, "vocabularyId");
            return (Criteria) this;
        }

        public Criteria andVocabularyIdNotEqualTo(Long value) {
            addCriterion("vocabulary_id <>", value, "vocabularyId");
            return (Criteria) this;
        }

        public Criteria andVocabularyIdGreaterThan(Long value) {
            addCriterion("vocabulary_id >", value, "vocabularyId");
            return (Criteria) this;
        }

        public Criteria andVocabularyIdGreaterThanOrEqualTo(Long value) {
            addCriterion("vocabulary_id >=", value, "vocabularyId");
            return (Criteria) this;
        }

        public Criteria andVocabularyIdLessThan(Long value) {
            addCriterion("vocabulary_id <", value, "vocabularyId");
            return (Criteria) this;
        }

        public Criteria andVocabularyIdLessThanOrEqualTo(Long value) {
            addCriterion("vocabulary_id <=", value, "vocabularyId");
            return (Criteria) this;
        }

        public Criteria andVocabularyIdIn(List<Long> values) {
            addCriterion("vocabulary_id in", values, "vocabularyId");
            return (Criteria) this;
        }

        public Criteria andVocabularyIdNotIn(List<Long> values) {
            addCriterion("vocabulary_id not in", values, "vocabularyId");
            return (Criteria) this;
        }

        public Criteria andVocabularyIdBetween(Long value1, Long value2) {
            addCriterion("vocabulary_id between", value1, value2, "vocabularyId");
            return (Criteria) this;
        }

        public Criteria andVocabularyIdNotBetween(Long value1, Long value2) {
            addCriterion("vocabulary_id not between", value1, value2, "vocabularyId");
            return (Criteria) this;
        }

        public Criteria andExampleIdIsNull() {
            addCriterion("example_id is null");
            return (Criteria) this;
        }

        public Criteria andExampleIdIsNotNull() {
            addCriterion("example_id is not null");
            return (Criteria) this;
        }

        public Criteria andExampleIdEqualTo(Long value) {
            addCriterion("example_id =", value, "exampleId");
            return (Criteria) this;
        }

        public Criteria andExampleIdNotEqualTo(Long value) {
            addCriterion("example_id <>", value, "exampleId");
            return (Criteria) this;
        }

        public Criteria andExampleIdGreaterThan(Long value) {
            addCriterion("example_id >", value, "exampleId");
            return (Criteria) this;
        }

        public Criteria andExampleIdGreaterThanOrEqualTo(Long value) {
            addCriterion("example_id >=", value, "exampleId");
            return (Criteria) this;
        }

        public Criteria andExampleIdLessThan(Long value) {
            addCriterion("example_id <", value, "exampleId");
            return (Criteria) this;
        }

        public Criteria andExampleIdLessThanOrEqualTo(Long value) {
            addCriterion("example_id <=", value, "exampleId");
            return (Criteria) this;
        }

        public Criteria andExampleIdIn(List<Long> values) {
            addCriterion("example_id in", values, "exampleId");
            return (Criteria) this;
        }

        public Criteria andExampleIdNotIn(List<Long> values) {
            addCriterion("example_id not in", values, "exampleId");
            return (Criteria) this;
        }

        public Criteria andExampleIdBetween(Long value1, Long value2) {
            addCriterion("example_id between", value1, value2, "exampleId");
            return (Criteria) this;
        }

        public Criteria andExampleIdNotBetween(Long value1, Long value2) {
            addCriterion("example_id not between", value1, value2, "exampleId");
            return (Criteria) this;
        }

        public Criteria andStatusIsNull() {
            addCriterion("status is null");
            return (Criteria) this;
        }

        public Criteria andStatusIsNotNull() {
            addCriterion("status is not null");
            return (Criteria) this;
        }

        public Criteria andStatusEqualTo(Integer value) {
            addCriterion("status =", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotEqualTo(Integer value) {
            addCriterion("status <>", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThan(Integer value) {
            addCriterion("status >", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("status >=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThan(Integer value) {
            addCriterion("status <", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThanOrEqualTo(Integer value) {
            addCriterion("status <=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusIn(List<Integer> values) {
            addCriterion("status in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotIn(List<Integer> values) {
            addCriterion("status not in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusBetween(Integer value1, Integer value2) {
            addCriterion("status between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("status not between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andLearnTimeIsNull() {
            addCriterion("learn_time is null");
            return (Criteria) this;
        }

        public Criteria andLearnTimeIsNotNull() {
            addCriterion("learn_time is not null");
            return (Criteria) this;
        }

        public Criteria andLearnTimeEqualTo(Date value) {
            addCriterion("learn_time =", value, "learnTime");
            return (Criteria) this;
        }

        public Criteria andLearnTimeNotEqualTo(Date value) {
            addCriterion("learn_time <>", value, "learnTime");
            return (Criteria) this;
        }

        public Criteria andLearnTimeGreaterThan(Date value) {
            addCriterion("learn_time >", value, "learnTime");
            return (Criteria) this;
        }

        public Criteria andLearnTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("learn_time >=", value, "learnTime");
            return (Criteria) this;
        }

        public Criteria andLearnTimeLessThan(Date value) {
            addCriterion("learn_time <", value, "learnTime");
            return (Criteria) this;
        }

        public Criteria andLearnTimeLessThanOrEqualTo(Date value) {
            addCriterion("learn_time <=", value, "learnTime");
            return (Criteria) this;
        }

        public Criteria andLearnTimeIn(List<Date> values) {
            addCriterion("learn_time in", values, "learnTime");
            return (Criteria) this;
        }

        public Criteria andLearnTimeNotIn(List<Date> values) {
            addCriterion("learn_time not in", values, "learnTime");
            return (Criteria) this;
        }

        public Criteria andLearnTimeBetween(Date value1, Date value2) {
            addCriterion("learn_time between", value1, value2, "learnTime");
            return (Criteria) this;
        }

        public Criteria andLearnTimeNotBetween(Date value1, Date value2) {
            addCriterion("learn_time not between", value1, value2, "learnTime");
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

        public Criteria andStudyCountIsNull() {
            addCriterion("study_count is null");
            return (Criteria) this;
        }

        public Criteria andStudyCountIsNotNull() {
            addCriterion("study_count is not null");
            return (Criteria) this;
        }

        public Criteria andStudyCountEqualTo(Integer value) {
            addCriterion("study_count =", value, "studyCount");
            return (Criteria) this;
        }

        public Criteria andStudyCountNotEqualTo(Integer value) {
            addCriterion("study_count <>", value, "studyCount");
            return (Criteria) this;
        }

        public Criteria andStudyCountGreaterThan(Integer value) {
            addCriterion("study_count >", value, "studyCount");
            return (Criteria) this;
        }

        public Criteria andTypeEqualTo(Integer type){
            addCriterion("type =",type ,"type");
            return (Criteria) this;
        }

        public Criteria andStudyCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("study_count >=", value, "studyCount");
            return (Criteria) this;
        }

        public Criteria andStudyCountLessThan(Integer value) {
            addCriterion("study_count <", value, "studyCount");
            return (Criteria) this;
        }

        public Criteria andStudyCountLessThanOrEqualTo(Integer value) {
            addCriterion("study_count <=", value, "studyCount");
            return (Criteria) this;
        }

        public Criteria andStudyCountIn(List<Integer> values) {
            addCriterion("study_count in", values, "studyCount");
            return (Criteria) this;
        }

        public Criteria andStudyCountNotIn(List<Integer> values) {
            addCriterion("study_count not in", values, "studyCount");
            return (Criteria) this;
        }

        public Criteria andStudyCountBetween(Integer value1, Integer value2) {
            addCriterion("study_count between", value1, value2, "studyCount");
            return (Criteria) this;
        }

        public Criteria andStudyCountNotBetween(Integer value1, Integer value2) {
            addCriterion("study_count not between", value1, value2, "studyCount");
            return (Criteria) this;
        }

        public Criteria andLearnCountIsNull() {
            addCriterion("learn_count is null");
            return (Criteria) this;
        }

        public Criteria andLearnCountIsNotNull() {
            addCriterion("learn_count is not null");
            return (Criteria) this;
        }

        public Criteria andLearnCountEqualTo(Integer value) {
            addCriterion("learn_count =", value, "learnCount");
            return (Criteria) this;
        }

        public Criteria andLearnCountNotEqualTo(Integer value) {
            addCriterion("learn_count <>", value, "learnCount");
            return (Criteria) this;
        }

        public Criteria andLearnCountGreaterThan(Integer value) {
            addCriterion("learn_count >", value, "learnCount");
            return (Criteria) this;
        }

        public Criteria andLearnCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("learn_count >=", value, "learnCount");
            return (Criteria) this;
        }

        public Criteria andLearnCountLessThan(Integer value) {
            addCriterion("learn_count <", value, "learnCount");
            return (Criteria) this;
        }

        public Criteria andLearnCountLessThanOrEqualTo(Integer value) {
            addCriterion("learn_count <=", value, "learnCount");
            return (Criteria) this;
        }

        public Criteria andLearnCountIn(List<Integer> values) {
            addCriterion("learn_count in", values, "learnCount");
            return (Criteria) this;
        }

        public Criteria andLearnCountNotIn(List<Integer> values) {
            addCriterion("learn_count not in", values, "learnCount");
            return (Criteria) this;
        }

        public Criteria andLearnCountBetween(Integer value1, Integer value2) {
            addCriterion("learn_count between", value1, value2, "learnCount");
            return (Criteria) this;
        }

        public Criteria andLearnCountNotBetween(Integer value1, Integer value2) {
            addCriterion("learn_count not between", value1, value2, "learnCount");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNull() {
            addCriterion("update_time is null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNotNull() {
            addCriterion("update_time is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeEqualTo(Date value) {
            addCriterion("update_time =", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotEqualTo(Date value) {
            addCriterion("update_time <>", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThan(Date value) {
            addCriterion("update_time >", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("update_time >=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThan(Date value) {
            addCriterion("update_time <", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThanOrEqualTo(Date value) {
            addCriterion("update_time <=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIn(List<Date> values) {
            addCriterion("update_time in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotIn(List<Date> values) {
            addCriterion("update_time not in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeBetween(Date value1, Date value2) {
            addCriterion("update_time between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotBetween(Date value1, Date value2) {
            addCriterion("update_time not between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andFirstStudyTimeIsNull() {
            addCriterion("first_study_time is null");
            return (Criteria) this;
        }

        public Criteria andFirstStudyTimeIsNotNull() {
            addCriterion("first_study_time is not null");
            return (Criteria) this;
        }

        public Criteria andFirstStudyTimeEqualTo(Date value) {
            addCriterion("first_study_time =", value, "firstStudyTime");
            return (Criteria) this;
        }

        public Criteria andFirstStudyTimeNotEqualTo(Date value) {
            addCriterion("first_study_time <>", value, "firstStudyTime");
            return (Criteria) this;
        }

        public Criteria andFirstStudyTimeGreaterThan(Date value) {
            addCriterion("first_study_time >", value, "firstStudyTime");
            return (Criteria) this;
        }

        public Criteria andFirstStudyTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("first_study_time >=", value, "firstStudyTime");
            return (Criteria) this;
        }

        public Criteria andFirstStudyTimeLessThan(Date value) {
            addCriterion("first_study_time <", value, "firstStudyTime");
            return (Criteria) this;
        }

        public Criteria andFirstStudyTimeLessThanOrEqualTo(Date value) {
            addCriterion("first_study_time <=", value, "firstStudyTime");
            return (Criteria) this;
        }

        public Criteria andFirstStudyTimeIn(List<Date> values) {
            addCriterion("first_study_time in", values, "firstStudyTime");
            return (Criteria) this;
        }

        public Criteria andFirstStudyTimeNotIn(List<Date> values) {
            addCriterion("first_study_time not in", values, "firstStudyTime");
            return (Criteria) this;
        }

        public Criteria andFirstStudyTimeBetween(Date value1, Date value2) {
            addCriterion("first_study_time between", value1, value2, "firstStudyTime");
            return (Criteria) this;
        }

        public Criteria andFirstStudyTimeNotBetween(Date value1, Date value2) {
            addCriterion("first_study_time not between", value1, value2, "firstStudyTime");
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