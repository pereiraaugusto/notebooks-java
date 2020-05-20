/*
 *  Copyright 2018 TWO SIGMA OPEN SOURCE, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import * as Big from 'big.js';
import * as _ from 'underscore';
import * as moment from 'moment-timezone/builds/moment-timezone-with-data.min';
import bkUtils from "../../../shared/bkUtils";
import DefaultAxis from "./DefaultAxis";

const NANOTIME_TYPE = 'nanotime';
const plotUtils = require('../../plotUtils');

export default class TimeAxis extends DefaultAxis {
  UNIT: number;
  SECOND: number;
  MINUTE: number;
  HOUR: number;
  DAY: number;
  MONTH: number;
  YEAR: number;

  constructor(type) {
    super(type);

    this.setUnits();
  }

  setUnits() {
    if (this.axisType === NANOTIME_TYPE) {
      this.UNIT = 1000000;
    } else {
      this.UNIT = 1;
    }

    this.SECOND = 1000 * this.UNIT;
    this.MINUTE = 1000 * 60 * this.UNIT;
    this.HOUR = 1000 * 60 * 60 * this.UNIT;
    this.DAY = 1000 * 60 * 60 * 24 * this.UNIT;
    this.MONTH = 1000 * 60 * 60 * 24 * 30 * this.UNIT;
    this.YEAR = 1000 * 60 * 60 * 24 * 365 * this.UNIT;
  }

  addIntervals(i:number, intervals: number[]) {
    let prev = intervals[intervals.length - 1];

    this.addTimeAxisIntervals(i, intervals, prev);
  }

  setRange(vl, vr, axisTimezone): void {
    if (vl !== null) { this.axisValL = vl; }
    if (vr !== null) { this.axisValR = vr; }

    this.setTimeAxisRange(axisTimezone);

    this.axisValSpan = plotUtils.minus(this.axisValR, this.axisValL);
  }

  setTimeAxisRange(axisTimezone) {
    if (axisTimezone != null) {
      this.axisTimezone = axisTimezone;
    }

    if (this.axisType === "time") {
      return;
    }

    // For nanotime
    if(!(this.axisValL instanceof Big)) {
      this.axisValL = new Big(this.axisValL);
    }

    if(!(this.axisValR instanceof Big)) {
      this.axisValR = new Big(this.axisValR);
    }
  }

  setGridlines(pointLeft, pointRight, count, marginLeft, marginRight) {
    if (pointRight < pointLeft) {
      console.error("cannot set right coord < left coord");

      return;
    }

    this.setAxisPct(pointLeft, pointRight);
    this.axisMarginValL = plotUtils.mult(this.axisValSpan, marginLeft);
    this.axisMarginValR = plotUtils.mult(this.axisValSpan, marginRight);

    let span = this.getSpan();

    this.setAxisSteps(span, this.dateIntws, {}, count);
    this.setLinesAndLabels(pointLeft, pointRight, span);
  }

  addTimeAxisIntervals(i: number, intervals: number[], prev: any) {
    if (i === 0) {
      intervals.push(1);
      intervals.push(5);

      return;
    }

    if (prev < this.UNIT) {
      intervals.push(prev + 5);
    } else if (prev === this.UNIT) {
      intervals.push(prev + 4 * this.UNIT);
    } else if (prev < this.SECOND) {
      intervals.push(prev + 5 * this.UNIT);
    } else if (prev === this.SECOND) {
      intervals.push(prev + this.SECOND * 4);
    } else if (prev < this.MINUTE) {
      intervals.push(prev + this.SECOND * 5);
    } else if (prev === this.MINUTE) {
      intervals.push(prev + this.MINUTE * 4);
    } else if (prev < this.HOUR) {
      intervals.push(prev + this.MINUTE * 5);
    }else if (prev < this.DAY) {
      intervals.push(prev + this.HOUR);
    } else if (prev < this.MONTH) {
      intervals.push(prev + this.DAY);
    } else if (prev < this.YEAR) {
      intervals.push(prev + this.DAY * 10);
    } else {
      intervals.push(prev + this.YEAR);
    }
  }

  calcLabels(lines, span) {
    let labels = [];

    if (this.axisType === "category") {
      labels = this.getCategoryAxisLabels(lines);
    } else {
      labels = this.getDefaultAxisLabels(lines, span);
    }

    if (this.shouldCalcTimeAxisLabels(labels, span)) {
      return this.getTimeAxisLabels(lines, span);
    }

    return {
      common : this.calcTimeAxisLabelsCommonPart(labels, span),
      labels : labels
    };
  }

  shouldCalcTimeAxisLabels(labels: string[], span: number): boolean {
    return (
      (
        span > this.SECOND && this.axisType === "time"
        || this.axisType === "nanotime" && plotUtils.gt(span, this.UNIT)
      )
      && labels.length != _.uniq(labels).length
    );
  }

  getTimeAxisLabels(lines: any[], span: number) {
    if (this.axisType === "nanotime" && plotUtils.lte(span, this.SECOND)){
      span = this.UNIT;
    } else if (plotUtils.lte(span, this.MINUTE)) {
      span = this.SECOND;
    } else if (plotUtils.lte(span, this.HOUR)) {
      span = this.MINUTE;
    } else if (plotUtils.lte(span, this.DAY)) {
      span = this.HOUR;
    } else if (plotUtils.lte(span, this.MONTH)) {
      span = this.DAY;
    } else if (plotUtils.lte(span, this.YEAR)) {
      span = this.MONTH;
    } else {
      span = this.YEAR;
    }

    if (this.axisType === NANOTIME_TYPE) {
      span = new Big(span).minus(1);
    } else {
      span -= 1;
    }

    return this.calcLabels(lines, span);
  }

  calcTimeAxisLabelsCommonPart(labels, span) {
    let common = '';

    if (
      (this.axisType !== "time" && this.axisType !== "nanotime")
      || !plotUtils.gte(span, this.HOUR)
      || labels.length <= 1
    ) {
      return common;
    }

    let tokens = labels[0].split(' ');
    let index = 0;

    const checkCommon = (index) => {
      let substring = common ? `${common} ${tokens[index]}` : tokens[index];

      for (let i = 1; i < labels.length; i++) {
        if (substring !== labels[i].substring(0, substring.length)) {
          return false;
        }
      }

      return true;
    };

    while (checkCommon(index)) {
      common = (common != '') ? common + ' ' + tokens[index] : tokens[index];
      index = index+1;
    }

    if (common.length > 1) {
      for (let i = 1; i < labels.length; i++) {
        let label = labels[i];

        if (common != label.substring(0, common.length)) {
          common = '';

          break;
        }
      }
    }

    if (common.length > 1) {
      for (let i = 0; i < labels.length; i++) {
        labels[i] = labels[i].replace(common, '').trim();
      }
    }

    return common.replace(',', '').trim();
  }

  getDefaultAxisLines(pointLeft, pointRight, axisStep) {
    let lines: number[] = [];
    let valueRight = this.getValue(pointRight);
    let value = this.getValue(pointLeft);

    if (value instanceof Big) {
      if(value.gte(0)){
        value = value.div(axisStep).round(0, 3).times(axisStep);
      } else {
        value = value.div(axisStep).round(0, 0).times(axisStep);
      }
    } else {
      value = this.normalizeValue(Math.ceil(value / axisStep) * axisStep, axisStep);
    }

    while (plotUtils.lte(value, valueRight) || plotUtils.lte(value, valueRight+1e-12)) {
      let pointCoords = this.getPercent(value);

      lines.push(pointCoords);
      value = this.normalizeValue(plotUtils.plus(value, axisStep), axisStep);
    }

    return lines;
  }

  normalizeValue(value, axisStep) {
    if (this.axisType !== "time" || !plotUtils.gt(axisStep, this.DAY)) {
      return value;
    }

    if (plotUtils.lte(axisStep, this.MONTH)) {
      value = this.selectStartOrEndInterval(value, "day");
    } else if (plotUtils.lte(axisStep, this.YEAR)) {
      value = this.selectStartOrEndInterval(value, "month");
    } else {
      value = this.selectStartOrEndInterval(value, "year");
    }

    return value;
  }

  selectStartOrEndInterval(value, interval) {
    const nextIntervalStart = bkUtils.applyTimezone(value, this.axisTimezone).endOf(interval).add(1, "ms");
    const intervalStart = bkUtils.applyTimezone(value, this.axisTimezone).startOf(interval);

    return  ((nextIntervalStart - value) > (value - intervalStart)) ? intervalStart : nextIntervalStart;
  }

  getString(pointCoords: number, span: number): string {
    return this.getTimeAxisStringValue(pointCoords, span);
  };

  getTimeAxisStringValue(pointCoords: number, span: number) {
    let value = this.getValue(pointCoords);
    let timestamp;
    let nanosec;

    if (this.axisType === "time") {
      timestamp = Math.ceil(value * 1000) / 1000;
    }

    else if (this.axisType === "nanotime") {
      timestamp = parseFloat(value.div(1000000).toFixed(0));
      nanosec = value.mod(1000000000).toFixed(0);
    }

    if (plotUtils.lte(span, this.SECOND) && this.axisType === "time") {
      return bkUtils.formatTimestamp(
        timestamp, this.axisTimezone, ".SSS"
      ) + ( (timestamp - Math.floor(timestamp)).toFixed(this.axisFixed));
    }

    if (plotUtils.lte(span, this.MINUTE) && this.axisType === "time") {
      return bkUtils.formatTimestamp(timestamp, this.axisTimezone, "mm:ss.SSS");
    }

    if (plotUtils.lte(span, this.HOUR)) {
      if (this.axisType !== "nanotime") {
        return bkUtils.formatTimestamp(timestamp, this.axisTimezone, "HH:mm:ss");
      }

      if (moment(timestamp) < this.SECOND) {
        return "." + plotUtils.padStr(nanosec, 9);
      }

      return bkUtils.formatTimestamp(timestamp, this.axisTimezone, "HH:mm:ss") + "." + plotUtils.padStr(nanosec, 9);
    }

    if (plotUtils.lte(span, this.DAY)) {
      return bkUtils.formatTimestamp(timestamp, this.axisTimezone, "YYYY MMM DD, HH:mm");
    }

    if (plotUtils.lte(span, this.MONTH)) {
      return bkUtils.formatTimestamp(timestamp, this.axisTimezone, "YYYY MMM DD");
    }

    if (plotUtils.lte(span, this.YEAR)) {
      return bkUtils.formatTimestamp(timestamp, this.axisTimezone, "YYYY MMM");
    }

    return bkUtils.formatTimestamp(timestamp, this.axisTimezone, "YYYY");
  }
}
