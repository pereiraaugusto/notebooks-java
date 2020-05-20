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

import widgets from './widgets';
export declare class PlotModel extends widgets.DOMWidgetModel {
    defaults(): any;
}
export declare class PlotView extends widgets.DOMWidgetView {
    render(): void;
    getNumberOfPointsForStandardPlot(plotModel: any): any;
    truncatePointsForStandardPlot(plotModel: any): void;
    limitPoints(plotModel: any): void;
    limitPointsForStandardPlot(plotModel: any, numberOfPoints: any): void;
    handleModellUpdate(): void;
    handleUpdateData(): void;
    initStandardPlot(model: any): void;
    initCombinedPlot(model: any): void;
}
declare const _default: {
    PlotModel: typeof PlotModel;
    PlotView: typeof PlotView;
};
export default _default;
