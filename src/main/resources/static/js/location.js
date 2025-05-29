$(function () {
    //위치 설정 클릭 시
    $('#setLocation').click(function () {
        if (confirm('현재위치를 가져오시겠습니까?')) {
            const modalEl = document.getElementById('headerMapModal');
            const bsModal = new bootstrap.Modal(modalEl);
            bsModal.show();

            openMap('headerMap', 'headerMapModal', 'headerAddressOutput');
        }
    });

    $('#headerMapModal').on('hidden.bs.modal', function () {
        // 이 코드는 모달이 완전히 닫힌 후 실행
        const status = $(this).data('locationStatus');

        if (status === 'success') {
            alert('위치 설정 완료!');
            location.href = "/home";
        } else if (status === 'error') {
            alert('위치 설정 실패!');
            // 실패 시에는 현재 페이지에 머무름
        } else {
            alert('위치 설정이 완료되지 않았거나 취소되었습니다.');
        }
    });
});


function openMap(targetMapId, modalId, addressOutputId) {
    const modalEl = document.getElementById(modalId);

    $(modalEl).data('locationStatus', 'pending');

    navigator.geolocation.getCurrentPosition(
        function (pos) {
            const lat = pos.coords.latitude;
            const lng = pos.coords.longitude;
            let areaName;

            const map = new google.maps.Map(document.getElementById(targetMapId), {
                center: {lat, lng},
                zoom: 15,
            });

            new google.maps.Marker({
                position: {lat, lng},
                map: map,
                title: "내 위치"
            });

            const geocoder = new google.maps.Geocoder();
            geocoder.geocode({location: {lat, lng}}, (results, status) => {
                if (status === "OK" && results[0]) {
                    for (const component of results[0].address_components) {
                        let types = component.types;

                        if (types.includes('sublocality_level_2') || types.includes('sublocality') || types.includes('neighborhood') || types.includes('administrative_area_level_3')) {
                            areaName = component.short_name;

                            break;
                        }
                    }


                    // const areaName = results[0].formatted_address;
                    document.getElementById(addressOutputId).textContent = areaName;

                    if (document.getElementById("area-name-label")) {
                        document.getElementById("area-name-label").textContent = `(${areaName})`;
                    }

                    const locationData = {
                        lat: lat,
                        lng: lng,
                        areaName: areaName,
                    }

                    const requestBody = new URLSearchParams(locationData);

                    fetch('/location', {
                        method: 'POST',
                        headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
                        body: requestBody,
                    })
                        .then(response => response.json())
                        .then(data => {
                            $(modalEl).data('locationStatus', 'success');
                        })

                    /*if (document.getElementById("areaName")) document.getElementById("areaName").value = areaName;
                    if (document.getElementById("latitude")) document.getElementById("latitude").value = lat;
                    if (document.getElementById("longitude")) document.getElementById("longitude").value = lng;*/
                }
            });
        },
        function (error) {
            console.log(error);
            $(modalEl).data('locationStatus', 'error'); // Geolocation API 실패
        });
}

/*var lat, lng, areaName;

$(function() {

    const api_key = "xxxx"

    $('button#getLocation').click(function (){
        navigator.geolocation.getCurrentPosition(
            (position) =>  {
                lat = position.coords.latitude;
                lng = position.coords.longitude;

                $('#showLat').text(lat);
                $('#showLng').text(lng);

                const map = new google.maps.Map(document.getElementById('showMap'), {
                    center: { lat, lng },
                    zoom: 13,
                });

                new google.maps.Marker({
                    position: { lat, lng },
                    map: map,
                    title: "내 위치"
                });

                // $('div#showLocation').text(`${lat}, ${lng}`);
                let url = `https://maps.googleapis.com/maps/api/geocode/json?latlng=${lat},${lng}&key=${api_key}&language=ko`;

                getAreaName(url);
            },
            (error) => {},
        )
    });
});

function getAreaName(url) {
    fetch(url)
        .then(response => response.json())
        .then(data => parseJSON(data))
}

function parseJSON(data) {
    if(data.status == "OK"){
        let result = data.results[0];

        for(const component of result.address_components){
            let types = component.types;

            if (types.includes('sublocality_level_2') || types.includes('sublocality') || types.includes('neighborhood') || types.includes('administrative_area_level_3')) {
                name = component.short_name;
                $('div#showLocation').text(name);
                areaName = name;

                break;
            }
        }

        const locationData = {
            lat: lat,
            lng: lng,
            areaName: areaName,
        }

        const requestBody = new URLSearchParams(locationData);

        fetch('/location', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
            body: requestBody,
        })
            .then(response => response.json())
    }
}*/
