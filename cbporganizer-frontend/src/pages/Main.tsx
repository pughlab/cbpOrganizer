import React from 'react';
import { TabView, TabPanel } from 'primereact/tabview';
import UploadComponent from "../Component/UploadComponent";
import ValidateComponent from "../Component/ValidateComponent";

const Main = () => {

    return (
        <div className="card">
            <TabView>
                <TabPanel header="Upload">
                    <UploadComponent/>
                </TabPanel>
                <TabPanel header="Validate">
                    <ValidateComponent />
                </TabPanel>
            </TabView>
        </div>
    );
}

export default Main;
