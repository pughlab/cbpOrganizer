import React, {useEffect, useState} from "react";
import useAxios from "../service/useAxios";
import { ProgressSpinner } from 'primereact/progressspinner';

const UploadComponent = () => {

    const { response, loading, error, operation } = useAxios();
    const { response: folderResponse, loading: folderLoading, error: folderError, operation: folderOperation } = useAxios();

    // status of tar.gz upload
    const [uploadMessage, setUploadMessage] = useState('');
    // list of studies uploaded
    const [folderList, setFolderList] = useState<any>([]);

    useEffect(() => {
        if (response !== null) {
            setUploadMessage("File uploaded successfully");
            getFolders();
        }
    }, [response]);

    useEffect(() => {
        if (error !== null) {
            setUploadMessage("Failed to upload study file");
        }
    }, [error]);

    useEffect(() => {
        if (folderResponse !== null) {
            setFolderList(folderResponse);
        }
    }, [folderResponse]);

    useEffect(() => {
        if (folderError !== null) {
            console.log('folderError: ', folderError);
        }
    }, [folderError]);

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

    return (
        <>
            <p>Upload a cBioPortal study tar.gz file</p>
            <input type="file" onChange={handleFileUpload} />
            <button onClick={getFolders}>listFiles</button>
            {(loading || folderLoading) && <div className="card flex justify-content-center">
                <ProgressSpinner />
            </div>}
            <br/>
            <br/>
            {uploadMessage && <p>{uploadMessage}</p>}
            {folderList.length > 0 && (
                <div>
                    <h3>Uploaded Studies:</h3>
                    <ul>
                        {folderList.map((folderName: any) => (
                            <li key={folderName}>{folderName}</li>
                        ))}
                    </ul>
                </div>
            )}
        </>
    )
}

export default UploadComponent
