<template>
  <SplitPane :min-percent="30" :default-percent="panelPercentVertical" split="vertical" @resize="handleVerticalSplitResize">
    <template slot="paneL">
      <el-table ref="interfaceTable" height="100%"
                :data="tableData.filter(dat => !apiSearch || dat.path.toLowerCase().includes(apiSearch.toLowerCase()) || dat.comment.toLowerCase().includes(apiSearch.toLowerCase()))"
                empty-text="No Api" highlight-current-row border lazy stripe
                @current-change="handleApiDataChange"
      >
        <el-table-column prop="id" width="24" :resizable="false">
          <template slot="header" class="dir-list-icon">
            <el-tooltip class="item" effect="dark" content="Directory" placement="right">
              <el-link @click="showToggle"><i class="el-icon-menu" /></el-link>
            </el-tooltip>
          </template>
          <template slot-scope="scope">
            <el-tooltip class="item" effect="dark" content="Choose to Test" placement="right">
              <el-checkbox v-model="scope.row.checked" name="type" @change="handleApiDataChange(scope.row)" />
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="Api" :show-overflow-tooltip="true" :resizable="false">
          <template slot="header" slot-scope="scope">
            <el-input v-model="apiSearch" size="mini" placeholder="search Api" />
          </template>
          <template slot-scope="scope">
            <el-tag size="mini" style="float: left;width: 45px;text-align: center;margin-right: 2px;" effect="dark" :type="tableRowMethodTagClassName(scope.row).css">
              {{ tableRowMethodTagClassName(scope.row).title }}
            </el-tag>
            <el-tag size="mini" style="float: left;width: 65px;text-align: center;" :type="tableRowStatusTagClassName(scope.row).css">
              {{ tableRowStatusTagClassName(scope.row).title }}
            </el-tag>
            <span style="overflow-x: hidden;">{{ requestPath(scope.row.path) }}&nbsp;&nbsp;&nbsp;&nbsp;</span>
            <span style="color: #adadad;display: contents;float: right; overflow-x: hidden;">[{{ scope.row.comment }}]</span>
          </template>
        </el-table-column>
        <el-table-column prop="id" width="24" :resizable="false">
          <template slot="header">
            <el-tooltip class="item" effect="dark" content="reload Api List" placement="right">
              <el-link @click="loadList"><i class="el-icon-refresh" /></el-link>
            </el-tooltip>
          </template>
          <template slot-scope="scope">
            <router-link :to="'/edit/' + scope.row.id">
              <el-tooltip class="item" effect="dark" content="Edit" placement="right">
                <el-link><i class="el-icon-edit" /></el-link>
              </el-tooltip>
            </router-link>
          </template>
        </el-table-column>
      </el-table>
      <el-tree v-show="directoryShow" id="directory-list" :default-expand-all="true" node-key="id" :data="directoryList" :props="defaultProps" @node-click="treeClick" />
    </template>
    <template slot="paneR">
      <split-pane :min-percent="30" :default-percent="panelPercentHorizontal" split="horizontal" @resize="handleHorizontalSplitResize">
        <template slot="paneL">
          <RequestPanel ref="listRequestPanel"
                        :header-data="headerData" :request-body="requestBody" :api-info="requestApiInfo"
                        @onRun="handleRun" @onHeaderChange="(data)=> { this.headerData = data}" @onRequestBodyChange="(data)=> { this.requestBody = data}"
          />
        </template>
        <template slot="paneR">
          <ResponsePanel ref="listResponsePanel"
                         :response-body="responseBody" :on-edit-page="false" :result-type="responseType"
                         @onResponseBodyChange="(data)=> { this.responseBody = data}"
          />
        </template>
      </split-pane>
    </template>
  </SplitPane>
</template>
<script>
import RequestPanel from '../components/RequestPanel';
import ResponsePanel from '../components/ResponsePanel';
import request from '../utils/request';
import {ApiUrl, contextPath} from '../utils/api-const';
import {checkRequestBody, errorBox, headerData, methodTagInfo, statusTagInfo} from '../utils/utils';

export default {
    components: {
        RequestPanel, ResponsePanel
    },
    data() {
        return {
            headerPanelHeight: '100%',
            panelPercentVertical: 50,
            panelPercentHorizontal: 50,
            loading: false,
            //
            apiSearch: '',
            tableData: [],
            directoryShow: false,
            directoryList: [],
            defaultProps: {
                children: 'children',
                label: 'label'
            },
            //
            headerData: [],
            requestApiInfo: {},
            requestBody: '{}',
            responseBody: '"empty."',
            responseType: 'json'
        };
    },
    mounted() {
        this.handleSplitResize(this.verticalPanelPercent, this.horizontalPanelPercent);
        //
        const self = this;
        this._resize = () => {
            return (() => {
                self.handleSplitResize(self.verticalPanelPercent, self.horizontalPanelPercent);
            })();
        };
        window.addEventListener('resize', this._resize);
        this.loadList();
    },
    beforeDestroy() {
        window.removeEventListener('resize', this._resize);
    },
    methods: {
        requestPath(apiPath) {
            return contextPath() + apiPath;
        },
        treeClick(obj, node, e) {
            this.apiSearch = obj.label;
            this.showToggle();
        },
        showToggle(e) {
            this.directoryShow = !this.directoryShow;
            const path = [];
            this.tableData.forEach(function (item, index) {
                path.push(item.path.replace(/^http[s]?:\/\//, '').replace(/\w*:?\w*/, '').replace(/\/$/, ''));
            });
            this.directoryList = this.buildChild(path, '');
            this.directoryList.splice(0, 0, {'label': '/'});
        },
        buildChild(pathList, label) {
            const tree = [];
            const labelKeySet = new Set();

            pathList.forEach(path => {
                if (path.indexOf(label) !== 0) {
                    return;
                }
                const subPath = path.substring(path.indexOf(label) + label.length);
                if (subPath.lastIndexOf('/') <= 0) {
                    return;
                }
                const subLabel = label + subPath.substring(0, subPath.indexOf('/', 1));
                if (labelKeySet.has(subLabel)) {
                    return;
                }
                labelKeySet.add(subLabel);
                tree.push({
                    'label': subLabel,
                    'children': this.buildChild(pathList, subLabel)
                });
            });
            return tree;
        },
        // 面板大小改变
        handleVerticalSplitResize(data) {
            this.handleSplitResize(data, this.panelPercentHorizontal);
        },
        handleHorizontalSplitResize(data) {
            this.handleSplitResize(this.panelPercentVertical, data);
        },
        handleSplitResize(verticalPercent, horizontalPercent) {
            if (verticalPercent === undefined) {
                verticalPercent = 50;
            }
            if (horizontalPercent === undefined) {
                horizontalPercent = 50;
            }
            this.panelPercentVertical = verticalPercent;
            this.panelPercentHorizontal = horizontalPercent;
            const horizontalDataNum = horizontalPercent / 100;
            const heightSize = document.documentElement.clientHeight - 60;
            const widthSize = (document.documentElement.clientWidth * (1 - (this.panelPercentVertical / 100)));
            //
            this.$refs.listRequestPanel.doLayout(heightSize * horizontalDataNum, widthSize);
            this.$refs.listResponsePanel.doLayout(heightSize * (1 - horizontalDataNum) + 10, widthSize);
        },
        // 选择了 Api 中的一个，确保只能选一个
        handleApiDataChange(row) {
            if (row === null || row === undefined) {
                return;
            }
            for (let i = 0; i < this.tableData.length; i++) {
                this.tableData[i].checked = row.id === this.tableData[i].id;
                if (this.tableData[i].checked) {
                    this.loadApi(this.tableData[i]);
                }
            }
        },
        tableRowStatusTagClassName(row) {
            return statusTagInfo(row.status);
        },
        tableRowMethodTagClassName(row) {
            return methodTagInfo(row.select);
        },
        // 加载列表
        loadList() {
            const self = this;
            request(ApiUrl.apiList, {
                'method': 'GET',
            }, response => {
                self.tableData = response.data.result;
                if (self.tableData && self.tableData.length > 0) {
                    self.tableData[0].checked = true;
                    self.$refs.interfaceTable.setCurrentRow(self.tableData[0]);
                }
            });
        },
        // 加载一个API
        loadApi(row) {
            if (row === null || row === undefined) {
                return;
            }
            const self = this;
            request(ApiUrl.apiInfo + '?id=' + row.id, {
                'method': 'GET'
            }, response => {
                // 如果服务端失败那么弹出错误消息
                if (!response.data.success) {
                    this.$message.error(`${response.data.code}: ${response.data.message}`);
                    return;
                }
                // 加载 API 信息
                const data = response.data.result;
                self.requestApiInfo = data;
                self.requestBody = data.requestBody || '{}';
                self.responseBody = data.responseBody || '"empty."';
                self.headerData = data.headerData || [];
                self.$nextTick(function () {
                    self.$refs.listRequestPanel.doUpdate();
                    self.$refs.listResponsePanel.doUpdate();
                });
            });
        },
        // 执行API调用
        handleRun() {
            if (!(this.requestApiInfo.status === 1 || this.requestApiInfo.status === 2)) {
                this.$message.error('Api must be Published or Changes.');
                return;
            }
            //
            const testResult = checkRequestBody(this.requestApiInfo.select, this.requestApiInfo.codeType, this.requestBody);
            if (!testResult) {
                return;
            }
            //
            const self = this;
            const requestURL = contextPath() + ('/' + this.requestApiInfo.path).replace('//', '/');
            request(requestURL, {
                'direct': true,
                'method': this.requestApiInfo.select,
                'headers': {
                    ...headerData(this.headerData),
                    'X-InterfaceUI-Info': 'true'
                },
                'data': JSON.parse(this.requestBody)
            }, response => {
                self.responseType = response.dataTypeMode;
                if (response.dataTypeMode === 'json') {
                    self.responseBody = JSON.stringify(response.data, null, 2);
                } else {
                    self.responseBody = response.data;
                }
                self.$nextTick(function () {
                    self.$refs.listResponsePanel.doUpdate();
                    self.$message({message: 'Success.', type: 'success'});
                });
            }, response => {
                errorBox(`${response.data.code}: ${response.data.message}`);
            });
        }
    }
};
</script>
<!-- Add "scoped" attribute to limit CSS to this component only -->
<style>
    #directory-list {
        top: 30px;
        left: -4px;
        position: absolute;
        width: 100%;
        height: 100%;
        z-index: 100;
        background: #fff;
    }

    .el-tree-node__content {
        border-bottom: 1px solid #ccc;
        line-height: 30px;
        background-color: #fbfbfb99;
    }

    /*.el-tree-node:nth-of-type(odd){
        background-color: #FAFAFA;
    }*/
    /*.has-gutter th:first-of-type{
        background-color: #ccc;
    }*/
</style>
