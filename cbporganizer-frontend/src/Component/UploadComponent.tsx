import React, {useEffect, useState} from "react";
import useAxios from "../service/useAxios";
import { ProgressSpinner } from 'primereact/progressspinner';
import {TreeTable} from "primereact/treetable";
import {Column} from "primereact/column";
import { v4 as uuidv4 } from 'uuid';

const UploadComponent = () => {

    const { response, loading, error, operation } = useAxios();
    const { response: folderResponse, loading: folderLoading, error: folderError, operation: folderOperation } = useAxios();
    const { response: filesResponse, loading: filesLoading, error: filesError, operation: filesOperation } = useAxios();

    // status of tar.gz upload
    const [uploadMessage, setUploadMessage] = useState('');
    // list of studies uploaded
    const [folderList, setFolderList] = useState<any>([]);
    // list of files for a given study
    // const [filesList, setFilesList] = useState<any>([]);
    // current node selected
    const [selectedNode, setSelectedNode] = useState<any>(null);

    useEffect(() => {
        if (response !== null) {
            setUploadMessage("File uploaded successfully");
        }
        getFolders();
    }, [response]);

    useEffect(() => {
        if (error !== null) {
            setUploadMessage("Failed to upload study file");
        }
    }, [error]);

    useEffect(() => {
        if (folderResponse !== null) {
            // @ts-ignore
            const folders = folderResponse.map((folder: any) =>
            {
                return {
                    key: uuidv4(),
                    data: {
                        name: folder
                    },
                    children: [{
                        key: uuidv4(),
                        data: {
                            name: 'placeHolder'
                        }
                    }]
                }
            });
            console.log(folders);
            setFolderList(folders);
        }
    }, [folderResponse]);

    useEffect(() => {
        if (filesResponse !== null) {
            // @ts-ignore
            const folderChildren = filesResponse.map((file: any) => {
                return {
                    key: uuidv4(),
                    data: {
                        name: file
                    }
                }
            });

            selectedNode.children = folderChildren;

            // find the index of the selected node, update the tree
            let _nodes = folderList.map((node: any) => {
                if (node.key === selectedNode.key) {
                    node = selectedNode;
                }
                return node;
            });
            setFolderList([..._nodes]);
        }
    }, [filesResponse]);

    useEffect(() => {
        if (folderError !== null) {
            console.log('folderError: ', folderError);
        }
        if (filesError !== null) {
            console.log('filesError: ', filesError);
        }
    }, [folderError, filesError]);

    const handleFileUpload = (event: any) => {
        const file = event.target.files[0];
        const formData = new FormData();
        formData.append("file", file);

        operation({
            method: 'POST',
            url: 'upload',
            headers: {
                'Content-type': 'multipart/form-data'
            },
            data: formData
        })
    };

    const getFolders = () => {
        folderOperation({
            method: 'GET',
            url: 'folders',
            headers: {
                'Content-type': 'application/json; charset=UTF-8',
            }
        })
    };

    const getFilesFromFolder = (name: string) => {
        filesOperation({
            method: 'GET',
            url: 'files/' + name,
            headers: {
                'Content-type': 'application/json; charset=UTF-8',
            }
        })
    };

    const onFolderExpand = (event: any) => {
        const folderNode = event.node;
        const folderName = folderNode.data.name;
        setSelectedNode(folderNode);
        getFilesFromFolder(folderName);
    };

    return (
        <>
            <p>Upload a cBioPortal study tar.gz file</p>
            <input type="file" onChange={handleFileUpload} />
            {(loading || folderLoading || filesLoading) && <div className="card flex justify-content-center">
                <ProgressSpinner />
            </div>}
            <br/>
            <br/>
            {uploadMessage && <p>{uploadMessage}</p>}
            <div>
                <TreeTable value={folderList} onExpand={onFolderExpand}>
                    <Column field="name" header="Name" expander />
                </TreeTable>
            </div>
        </>
    )
}

export default UploadComponent
